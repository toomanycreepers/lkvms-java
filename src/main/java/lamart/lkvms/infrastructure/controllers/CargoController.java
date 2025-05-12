package lamart.lkvms.infrastructure.controllers;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletResponse;
import lamart.lkvms.application.dtos.CargoDetailDto;
import lamart.lkvms.application.dtos.CargoStatusCountsDto;
import lamart.lkvms.application.dtos.CargoTrackingTimelineDto;
import lamart.lkvms.application.dtos.LatestCargoDto;
import lamart.lkvms.application.dtos.SlimCargoDto;
import lamart.lkvms.application.services.logistic.CargoService;
import lamart.lkvms.application.services.logistic.ExportService;
import lamart.lkvms.application.services.user.UserService;
import lamart.lkvms.core.entities.logistic.Cargo;
import lamart.lkvms.core.entities.logistic.Feedback;
import lamart.lkvms.core.entities.logistic.Organization;
import lamart.lkvms.core.entities.user.User;
import lamart.lkvms.core.repositories.CargoRepository;
import lamart.lkvms.core.repositories.FeedbackRepository;
import lamart.lkvms.infrastructure.auth.AuthUtils;


@RestController
@RequestMapping("/api/logistics/cargo")
public class CargoController {

    private final CargoService cargoService;
    private final ExportService exportService;
    private final FeedbackRepository feedbackRepository;
    private final CargoRepository cargoRepository;
    private final UserService userService;

    CargoController(CargoService cargoService, ExportService exportService, FeedbackRepository feedbackRepository, CargoRepository cargoRepository, UserService userService) {
        this.cargoService = cargoService;
        this.exportService = exportService;
        this.feedbackRepository = feedbackRepository;
        this.cargoRepository = cargoRepository;
        this.userService = userService;
    }

    @GetMapping("/")
    public Page<SlimCargoDto> getCargos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean dbu) {

        Organization org = AuthUtils.getCurrentOrganization(userService);
        return cargoService.getCargos(page, size, status, dbu, search, org);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CargoDetailDto> getCargoDetail(@PathVariable String id) {
        Organization organization = AuthUtils.getCurrentOrganization(userService);
        return ResponseEntity.ok(CargoDetailDto.fromEntity(cargoService.getCargoDetails(
            Long.parseLong(id), 
            organization
        )));
    }
    
    @GetMapping("/{id}/tracking")
    public ResponseEntity<CargoTrackingTimelineDto> trackCargo(@PathVariable Long id) {
        Organization organization = AuthUtils.getCurrentOrganization(userService);
        return ResponseEntity.ok(cargoService.getCargoTracking(id, organization));
    }
    
    @GetMapping("/latest")
    public ResponseEntity<List<LatestCargoDto>> getLatestCargos() {
        
        Organization organization = AuthUtils.getCurrentOrganization(userService);
        try{
            return ResponseEntity.ok(cargoService.getLatestCargos(organization));
        }
        catch (BadRequestException e){
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/export")
    public void exportCargos(HttpServletResponse response) throws IOException {
        Organization organization = AuthUtils.getCurrentOrganization(userService);
        List<Cargo> cargos = cargoService.getCargosByOrganization(organization);
        exportService.exportCargos(response, cargos);
    }
    
    @GetMapping("/statuses")
    public ResponseEntity<CargoStatusCountsDto> getCargoStatusAmounts() {
        Organization organization = AuthUtils.getCurrentOrganization(userService);
        if (organization == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No organization selected");
        }

        return ResponseEntity.ok(cargoService.getStatusCounts(organization));
    }
    
    @PostMapping("/feedback")
    public ResponseEntity<?> createOrUpdateFeedback(
            @RequestBody FeedbackRequest request
    ) {
        User currentUser;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        if (principal instanceof UserDetails) {
            currentUser = userService.getUserByEmail(((UserDetails)principal).getUsername());
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
            "User is not logged in");
        }

        Organization organization = AuthUtils.getCurrentOrganization(userService);

        Cargo cargo = cargoRepository.findById(request.cargoId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cargo not found"));

        if (!organization.getMembers().stream().map(User::getId).toList().contains(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "User is not a member of the organization");
        }

        Optional<Feedback> existingFeedback = feedbackRepository
            .findByAuthorAndRelatedCargo(currentUser, cargo);

        existingFeedback.ifPresent(feedback -> {
            if (Duration.between(feedback.getDateUpdated(), LocalDateTime.now()).toMinutes() < 30) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Your requests are too frequent, try again later");
            }
        });

        Feedback feedback = existingFeedback.orElseGet(Feedback::new);
        feedback.setText(request.text);
        feedback.setRating(request.rating);
        feedback.setAuthor(currentUser);
        feedback.setRelatedCargo(cargo);
        
        Feedback savedFeedback = feedbackRepository.save(feedback);

        return ResponseEntity
            .status(existingFeedback.isPresent() ? HttpStatus.OK : HttpStatus.CREATED)
            .body(Map.of(
                "status", existingFeedback.isPresent() ? "Updated" : "Created",
                "id", savedFeedback.getId()
            ));
    }

    public record FeedbackRequest(
        Long cargoId,
        String text,
        int rating
    ) {}
}
