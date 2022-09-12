package io.nixify.api.repos

import io.nixify.api.model.DerivationNode
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.stereotype.Repository

@Repository
interface DerivationRepository : Neo4jRepository<DerivationNode, String>  {
    fun findByHash(hash: String) : DerivationNode?
}
