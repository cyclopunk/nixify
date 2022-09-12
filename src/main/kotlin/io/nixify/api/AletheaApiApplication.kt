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
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.event.TransactionalEventListener
import org.springframework.web.servlet.config.annotation.EnableWebMvc

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
    
    @Autowired
    lateinit var store : NixStore
    
    
    
    @TransactionalEventListener(ApplicationStartedEvent::class, fallbackExecution = true)
    fun appStartup() {
        dnodes.deleteAll()
        pkgNodes.deleteAll()
    
        log.i("Loading nix store")
        store.load()
        log.i("Done loading nix store")
    
        // build the graph
        log.i("Building graph there are ${store.children} total edges and ${store.derivations.size} nodes")
    
        //store.packages.forEach { (k,v) -> pkgNodes.save(v) }
        store.derivations.forEach { (k,v) ->
            if (store.nodeCache[k] == null)
                store.makeDerivationNode(k, v)
        }
    
    
        log.i("Done building graph - saving to neo4j")
    
        dnodes.saveAll(store.nodeCache.values)
    }
}

fun main(args: Array<String>) {
    runApplication<AletheaApiApplication>(*args)
}
