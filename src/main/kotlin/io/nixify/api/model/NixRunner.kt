package io.nixify.api.model

object NixRunner {
    fun showDerivation(path: String) : String {
       return runCommand("show-derivation", path)
    }
    
    fun runCommand(cmd: String, vararg args: String) : String {
        val pb = ProcessBuilder("nix","--extra-experimental-features","nix-command", cmd, *args)
        val process = pb.start()
    
        return String(process.inputStream.readAllBytes())
    }
}