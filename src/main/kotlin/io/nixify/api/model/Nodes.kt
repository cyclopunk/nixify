package io.nixify.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.neo4j.core.schema.CompositeProperty
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship

@Node
class PackageNode (
    var name : String,
    var location: String,
    @Id
    var hash: String,
    var files: MutableSet<String> = mutableSetOf(),
    @Relationship(type = "LINKED_TO")
    var children: MutableList<PackageNode> = mutableListOf(),
    var size: Long = 0
)

// neo4j entity
@Node
class DerivationNode (
    val name: String,
    @Id
    val hash: String,
    @Relationship("INPUTS")
    val inputDerivations: List<DerivationNode>,
    val system : String,
    val builder: String,
    val args: String,
    @Relationship("OUTPUT")
    val outputs: List<PackageNode>,
    @CompositeProperty()
    val environment: Map<String, String>,
    @Relationship("PARENTS")
    val parentDerivations: MutableList<DerivationNode> = mutableListOf(),
    @Version
    var version : Long? = null
    ) {
    override fun toString(): String {
        return "$name ($hash)"
    }
    
    override fun equals(other: Any?): Boolean {
        if (other !is DerivationNode) return false
        
        return other.hash == this.hash
    }
    
    override fun hashCode(): Int {
        return this.hash.hashCode()
    }
}

class EnvironmentNode (
    @Id
    val key: String,
    val values: String
    )

data class Output (val path: String?, val hashAlgo: String?, val hash: String?)
// Output from a nix show-derivation
class DerivationFile (
    val outputs: MutableMap<String, Output>,
    @JsonProperty("inputSrcs")
    val inputSources: List<String>,
    @JsonProperty("inputDrvs")
    val inputDerivations: MutableMap<String, List<String>>,
    val system : String,
    val builder: String,
    @JsonProperty("args")
    val arguments: List<String>,
    @JsonProperty("env")
    val environment: MutableMap<String, String>,
    @JsonIgnore
    var name: String? = null
    ){}