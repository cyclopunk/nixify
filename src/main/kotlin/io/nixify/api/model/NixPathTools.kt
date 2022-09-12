package io.nixify.api.model

object NixPathTools {
      fun splitPath(path : String, storeLocation: String = "$DEFAULT_STORE_LOCATION/") : NameHash {
        val result = path.replace(storeLocation,"")
            .split(Regex("/")).first()
            .split(Regex("-"), 2)
         // println("$path " + result)
          return NameHash(result[1], result[0])
      }
    fun extractDirectory(path: String, storeLocation: String = DEFAULT_STORE_LOCATION): String {
    
        return path.replace(storeLocation, "")
            .split(Regex("/"), 2).last()
    }
}