package io.nixify.api.model

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File

const val DEFAULT_STORE_LOCATION : String = "/nix/store"

data class NameHash (val name: String, val hash:String)

class NixStore (
    var children: Long = 0,
    var packages : MutableMap<String, PackageNode> = mutableMapOf(),
    var derivations : MutableMap<String, DerivationFile> = mutableMapOf()
        ) {
        fun findPackage(hash: String) : PackageNode? {
            return packages[hash]
        }
        fun findDerivation(hash: String) : DerivationFile? {
            return derivations[hash]
        }
        companion object {
            val mapper = jacksonObjectMapper()
        
            // walks the store tree and creates derivations and packages.
            fun load(path: String = DEFAULT_STORE_LOCATION): NixStore {
                val store = NixStore()
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
                        
                        store.derivations[nh.hash] = node
                        
                        store.children += node.inputDerivations.size
                    } else {
                        // file
                        val pkg = store.findPackage(nh.hash) ?: PackageNode(nh.name, it.canonicalPath,
                            nh.hash)
                        pkg.files.add(NixPathTools.extractDirectory(it.canonicalPath))
                        pkg.size += it.length()
                        
                        store.packages[nh.hash] = pkg
                    }
                    
                }
                
                return store
            }
        }
}

