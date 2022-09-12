package io.nixify.api.controllers

import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@Secured
@RestController("/nix")
class NixController {
    @PostMapping("run")
    fun buildNixGraph(@AuthenticationPrincipal oidcUser: OidcUser) {
        if (!oidcUser.getClaimAsBoolean("admin")) {
            return
        }
        println(oidcUser)
    }
}