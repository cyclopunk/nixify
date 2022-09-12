package io.nixify.api

import io.nixify.api.model.NixRunner
import io.nixify.api.model.NixStore
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UtilsTests {
    @Test
    fun testNixStoreWalk() {
        val store = NixStore.load()

    }
    
    @Test
    fun testNixRunner() {
        println(NixRunner.showDerivation("/nix/store/zlf39g3a9b443h453y7l849ysxhbmzqw-gc-8.0.6.tar.gz.drv"))
    }
}