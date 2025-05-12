package lamart.lkvms.infrastructure.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import lamart.lkvms.application.dtos.NotificationSettingsDto;
import lamart.lkvms.application.mappers.NotificationSettingsMapper;
import lamart.lkvms.application.services.logistic.CargoService;
import lamart.lkvms.application.services.user.NotificationService;
import lamart.lkvms.application.services.user.UserService;
import lamart.lkvms.core.entities.user.User;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final UserService userService;
    private final NotificationService settingsService;
    private final CargoService cargoService;

    NotificationController(UserService userService, NotificationService settingsService, CargoService cargoService) {
        this.userService = userService;
        this.settingsService = settingsService;
        this.cargoService = cargoService;
    }

    @GetMapping("/default/")
    public List<NotificationSettingsDto> getDefaultUserNotificationSettings(
        @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        return settingsService.getOrCreateNotificationSettings(
                user.getId(),
        null, 
        false)
        .stream().map(NotificationSettingsMapper::toDto).toList();
    }

    @PutMapping("/default/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateDefaultUserNotificationSettings(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody List<NotificationSettingsDto> settingsDtos) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        settingsService.updateSettingsInBulk(user.getId(), settingsDtos, false);
    }
    
    @GetMapping("/cargo/")
    public List<NotificationSettingsDto> getCargoNotificationsSettings(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long cargo) {
        
        if (!cargoService.doesCargoExist(cargo)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cargo with this id does not exist.");
        }
        
        return settingsService.getOrCreateNotificationSettings(
            userService.getUserByEmail(userDetails.getUsername()).getId(),
            cargo,
            false
        ).stream().map(NotificationSettingsMapper::toDto).toList();
    }

    @PutMapping("/cargo/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCargoNotificationsSettings(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long cargo,
            @Valid @RequestBody List<NotificationSettingsDto> settingsDtos) {
        
        settingsService.updateCargoSettings(
            userService.getUserByEmail(userDetails.getUsername()).getId(),
            cargo,
            settingsDtos,
            true
        );
    }

    @DeleteMapping("/cargo/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCargoNotificationsSettings(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long cargo) {
        
        settingsService.deleteSpecificCargoNotificationSettings(
            userService.getUserByEmail(userDetails.getUsername()).getId(),
            cargo
        );
    }
    
    @DeleteMapping("/cargo/reset/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetCargoNotificationSettings(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        settingsService.deleteAllCargoNotificationSettings(user.getId());
    }
}