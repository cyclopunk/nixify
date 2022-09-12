package io.nixify.api.model

/**
 * Class to run Nix commands locally
 *
 * TODO Have the individual commands return entities (e.g. DerivationFile)
 */
object NixRunner {
    /**
     * Run show-derivation on a path
     *
     * TODO add input validation && better error handling e.g. if paths are not correct
     */
    fun showDerivation(path: String) : String {
       return runCommand("show-derivation", path)
    }
    
    /**
     * Run a command with arguments.
     *
     * TODO extend to add other extra experimental features (properties?)
     */
    private fun runCommand(cmd: String, vararg args: String) : String {
        val pb = ProcessBuilder("nix","--extra-experimental-features","nix-command", cmd, *args)
        val process = pb.start()
    
        return String(process.inputStream.readAllBytes())
    }
}