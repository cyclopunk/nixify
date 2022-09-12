package io.nixify.api

import io.nixify.api.model.DerivationFile
import io.nixify.api.model.DerivationNode
import io.nixify.api.model.NixPathTools
import io.nixify.api.model.NixStore
import io.nixify.api.repos.DerivationRepository
import io.nixify.api.repos.PackageRepository
import io.nixify.api.services.ILoggingService
import org.neo4j.driver.Driver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.event.TransactionalEventListener

@SpringBootApplication
@EnableNeo4jRepositories
@EnableTransactionManagement
@EntityScan
class AletheaApiApplication {
    
    @Autowired
    lateinit var log : ILoggingService
    @Autowired
    lateinit var dnodes : DerivationRepository
    @Autowired
    lateinit var pkgNodes : PackageRepository
    
    lateinit var store : NixStore
    
    var nodeCache : MutableMap<String, DerivationNode> = mutableMapOf()
    
    fun makeDerivationNode(hash: String, file: DerivationFile) : DerivationNode? {
        //println("Making node $hash - ${file.name}")
        
        val children = mutableListOf<DerivationNode>()
        
        val packageId = NixPathTools.splitPath(file.outputs.values.first().path ?: "").hash
        
        val pkg = store.findPackage(packageId)
        
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
                childNode = makeDerivationNode(nh.hash,store.findDerivation(nh.hash)!!)?.let {
                    //log.i("Saving derivation in cache ${it.name} hash: ${it.hash}")
                    
                    nodeCache[it.hash] = it
                    
                    if (nodeCache.size % 10 == 0)
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
    
    @TransactionalEventListener(ApplicationStartedEvent::class, fallbackExecution = true)
    fun appStartup() {
        dnodes.deleteAll()
        pkgNodes.deleteAll()
        
        log.i("Loading nix store")
        store = NixStore.load()
        log.i("Done loading nix store")
        
        // build the graph
        log.i("Building graph there are ${store.children} total edges and ${store.derivations.size} nodes")
        
        //store.packages.forEach { (k,v) -> pkgNodes.save(v) }
        store.derivations.forEach { (k,v) ->
            if (nodeCache[k] == null)
                makeDerivationNode(k, v)
        }
        
        
        log.i("Done building graph - saving to neo4j")
        
        dnodes.saveAll(nodeCache.values)
        
        
        
    }
}

fun main(args: Array<String>) {
    runApplication<AletheaApiApplication>(*args)
}
