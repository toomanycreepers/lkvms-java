package lamart.lkvms.infrastructure.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lamart.lkvms.application.dtos.ManagerDto;
import lamart.lkvms.application.dtos.OrganizationWithMembersDto;
import lamart.lkvms.application.dtos.SkinnyOrganizationDto;
import lamart.lkvms.application.services.logistic.OrganizationService;
import lamart.lkvms.application.services.user.UserService;
import lamart.lkvms.core.entities.logistic.Organization;
import lamart.lkvms.core.entities.user.User;

@RestController
@RequestMapping("/api/logistics/organization")
public class OrganizationController {

    private final UserService userService;
    private final OrganizationService service;

    OrganizationController(UserService userService, OrganizationService service) {
        this.userService = userService;
        this.service = service;
    }

    @GetMapping("/")
    public ResponseEntity<List<SkinnyOrganizationDto>> getOrganizations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = userService.getUserByEmail(((UserDetails) authentication.getPrincipal()).getUsername()).getId();
        return ResponseEntity.ok(service.getUserOrganizations(userId));
    }
    
    @GetMapping("/{id}/")
    public ResponseEntity<?> getOrganizationDetail(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByEmail(((UserDetails) authentication.getPrincipal()).getUsername());
        if ( user.getOrganizations().stream().map(Organization::getId).toList().contains(id) ) {
            OrganizationWithMembersDto dto = service.getOrganizationDetails(id);
            return ResponseEntity.ok(dto);
        }
        return new ResponseEntity<>(Map.of("message", "User is not member of organization"), HttpStatus.FORBIDDEN);
    }
    
    @GetMapping("/manager")
    public ResponseEntity<ManagerDto> getCurrentManager() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user;
        if (authentication.getPrincipal() instanceof UserDetails) {
            user = userService.getUserByEmail(((UserDetails) authentication.getPrincipal()).getUsername());
            ManagerDto dto = service.getCurrentManagerForUser(user.getSelectedOrganization().getId());
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.badRequest().build();
    }
}
