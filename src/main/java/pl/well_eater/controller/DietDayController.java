package pl.well_eater.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pl.well_eater.dto.DietDayDTO;
import pl.well_eater.exception.EntityNotFoundException;
import pl.well_eater.exception.UnauthorizedRequestException;
import pl.well_eater.request.CreateDietDayRequest;
import pl.well_eater.security.CurrentUser;
import pl.well_eater.service.DietDayFacade;

import java.net.URI;
import java.time.LocalDate;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/diet-day")
public class DietDayController {

    public final DietDayFacade dietDayFacade;

    @PostMapping("/create")
    public ResponseEntity<?> createDietDay(@RequestBody @NotNull final CreateDietDayRequest request,
                                           @CurrentUser final UserDetails principal) {
        try {
            DietDayDTO dietDayDTO = dietDayFacade.createDietDay(request, principal);
            return ResponseEntity.created(new URI("api/diet-day/get/" + dietDayDTO.getDietDayId())).body(dietDayDTO);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/get/{dietDayId}")
    public ResponseEntity<?> getDietDay(@Valid @PathVariable("dietDayId") final Long dietDayId,
                                           @CurrentUser final UserDetails principal) {
        try {
            return ResponseEntity.ok(dietDayFacade.getDietDay(dietDayId, principal));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedRequestException e) {
            return ResponseEntity.status(e.getStatus()).build();
        }
    }

    @GetMapping("/get/by-date")
    public ResponseEntity<?> getDietDays(@RequestParam final LocalDate startDate,
                                         @RequestParam final LocalDate endDate,
                                         @CurrentUser final UserDetails principal) {
        return ResponseEntity.ok(dietDayFacade.getDietDaysBetween(startDate, endDate, principal));
    }

    @DeleteMapping("/delete/{dietDayId}")
    public ResponseEntity<?> deleteDietDay(@Valid @PathVariable("dietDayId") final Long dietDayId,
                                           @CurrentUser final UserDetails principal) {
        try {
            dietDayFacade.deleteDietDay(dietDayId, principal);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedRequestException e) {
            return ResponseEntity.status(e.getStatus()).build();
        }
    }

    @GetMapping("/get/{dietDayId}/stats")
    public ResponseEntity<?> getDietDayStats(@Valid @PathVariable("dietDayId") final Long dietDayId,
                                            @CurrentUser final UserDetails principal) {
        try {
            return ResponseEntity.ok(dietDayFacade.calculateDayStats(dietDayId, principal));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedRequestException e) {
            return ResponseEntity.status(e.getStatus()).build();
        }
    }

    @GetMapping("/get/by-date/stats")
    public ResponseEntity<?> getDietDaysStats(@RequestParam final LocalDate startDate,
                                              @RequestParam final LocalDate endDate,
                                              @CurrentUser final UserDetails principal) {
        return ResponseEntity.ok(dietDayFacade.calculateDaysStats(startDate, endDate, principal));
    }
}
