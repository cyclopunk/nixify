package io.nixify.api.controllers

import io.nixify.api.model.DerivationNode
import io.nixify.api.model.NixStore
import io.nixify.api.repos.DerivationRepository
import io.nixify.api.repos.PackageRepository
import io.nixify.api.services.ILoggingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@Secured
@RestController
@RequestMapping("/nix")
class NixController {
    @Autowired
    lateinit var log : ILoggingService
    @Autowired
    lateinit var dnodes : DerivationRepository
    @Autowired
    lateinit var pkgNodes : PackageRepository
    
    @Autowired
    lateinit var store : NixStore
    
    var nodeCache : MutableMap<String, DerivationNode> = mutableMapOf()
    
    @GetMapping("run")
    fun run(@AuthenticationPrincipal  oidcUser : OidcUser) {
    
    }
}