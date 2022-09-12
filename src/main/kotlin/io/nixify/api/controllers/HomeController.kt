package io.nixify.api.controllers

import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Secured
class HomeController {
    @GetMapping("/")
    fun home(@AuthenticationPrincipal  oidcUser : OidcUser) : String{
        return "Hi, ${oidcUser.email}"
    }
}