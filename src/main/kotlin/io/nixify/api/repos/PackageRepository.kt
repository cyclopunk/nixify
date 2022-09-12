package io.nixify.api.repos

import io.nixify.api.model.PackageNode
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.stereotype.Repository

@Repository
interface PackageRepository : Neo4jRepository<PackageNode, String> {
}