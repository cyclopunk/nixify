package io.nixify.api.model

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.nixify.api.services.ILoggingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File

const val DEFAULT_STORE_LOCATION : String = "/nix/store"

data class NameHash (val name: String, val hash:String)

@Service
class NixStore (
    
    var children: Long = 0,
    var packages : MutableMap<String, PackageNode> = mutableMapOf(),
    var derivations : MutableMap<String, DerivationFile> = mutableMapOf()
        ) {
    
        @Autowired
        lateinit var log : ILoggingService
        val mapper = jacksonObjectMapper()
    
        fun findPackage(hash: String) : PackageNode? {
            return packages[hash]
        }
        fun findDerivation(hash: String) : DerivationFile? {
            return derivations[hash]
        }
    
        var nodeCache : MutableMap<String, DerivationNode> = mutableMapOf()
        fun makeDerivationNode(hash: String, file: DerivationFile) : DerivationNode? {
            //println("Making node $hash - ${file.name}")
        
            val children = mutableListOf<DerivationNode>()
        
            val packageId = NixPathTools.splitPath(file.outputs.values.first().path ?: "").hash
        
            val pkg = this.findPackage(packageId)
        
            if (pkg != null){
                println("Found output package $pkg")
            }
        
            val node = DerivationNode(file.name ?: "",hash, children, file.system,
                file.builder,
                file.arguments.joinToString(" "),
                pkg?.let {  listOf(pkg) } ?: listOf(),
                file.environment
            )
        
            //log.i("Adding children to ${node.name}")
        
            children.addAll(file.inputDerivations.mapNotNull {(k,v) ->
                val nh = NixPathTools.splitPath(k)
                //println("Adding child $k")
                var childNode = nodeCache[nh.hash]
            
                if (childNode == null) {
                    childNode = makeDerivationNode(nh.hash,this.findDerivation(nh.hash)!!)?.let {
                        //log.i("Saving derivation in cache ${it.name} hash: ${it.hash}")
                    
                        nodeCache[it.hash] = it
                    
                        if (nodeCache.size % 100 == 0)
                            log.i("Current nodeCache size: ${nodeCache.size}")
                    
                        it
                    }
                }
            
                childNode
            }.toList())
        
            /*try {
                children.forEach {
                    dnodes.save(it)
                }
        
                dnodes.save(node)
            } catch (e: Exception) {
                println("Can't save node ${node} - $e")
            }*/
            return node
        }
    
    // walks the store tree and creates derivations and packages.
    fun load(path: String = DEFAULT_STORE_LOCATION): NixStore {
        val typeRef = object: TypeReference<Map<String, DerivationFile>>() {
        
        }
        
        File(path).walkTopDown().forEach {
            
            // skip list
            if (listOf(path, "$path/.links").contains(it.canonicalPath)){
                return@forEach
            }
            
            val nh = NixPathTools.splitPath(it.canonicalPath)
            
            if (nh.name.endsWith(".drv")) {
                val dv = mapper.readValue(NixRunner.showDerivation(it.path), typeRef)
                val node = dv.values.first()
                
                node.name = nh.name
                
                this.derivations[nh.hash] = node
    
                this.children += node.inputDerivations.size
            } else {
                // file
                val pkg = this.findPackage(nh.hash) ?: PackageNode(nh.name, it.canonicalPath,
                    nh.hash)
                pkg.files.add(NixPathTools.extractDirectory(it.canonicalPath))
                pkg.size += it.length()
    
                this.packages[nh.hash] = pkg
            }
            
        }
        
        return this
    }
}

