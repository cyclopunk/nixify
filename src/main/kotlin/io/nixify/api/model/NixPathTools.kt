package io.nixify.api.model

object NixPathTools {
    /**
     * Split a nix store path into a NameHash (pair of Name and Hash)
     */
      fun splitPath(path : String, storeLocation: String = "$DEFAULT_STORE_LOCATION/") : NameHash {
        val result = path.replace(storeLocation,"")
            .split(Regex("/")).first()
            .split(Regex("-"), 2)
         // println("$path " + result)
          return NameHash(result[1], result[0])
      }
     
    /**
     * Extract the full directory after the store location and hash/name
     *
     * @return the full path after /nix/store/$hash-$name
     */
    fun extractDirectory(path: String, storeLocation: String = DEFAULT_STORE_LOCATION): String {
    
        return path.replace(storeLocation, "")
            .split(Regex("/"), 2).last()
    }
}