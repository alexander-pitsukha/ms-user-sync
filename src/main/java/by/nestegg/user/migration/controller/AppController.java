package by.nestegg.user.migration.controller;

import by.nestegg.user.migration.service.UserMigrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "App controller", description = "Application resources that provides access to available application data")
@RestController
@RequestMapping("migration")
@RequiredArgsConstructor
@Slf4j
public class AppController {

    private final UserMigrationService userMigrationService;

    @Operation(summary = "Start users migration", description = "Provides starting users migration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return HttpStatus",
                    content = {@Content(mediaType = "application/json", schema = @Schema())})})
    @GetMapping("start")
    public ResponseEntity<HttpStatus> startMigration() {
        log.info("Start users migration");
        userMigrationService.usersMigration();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "User migration", description = "Provides users migration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return HttpStatus",
                    content = {@Content(mediaType = "application/json", schema = @Schema())})})
    @PostMapping("migrate/{externalId}")
    public ResponseEntity<HttpStatus> userMigration(@PathVariable UUID externalId) {
        log.info("Start user migration with externalId: {}", externalId);
        userMigrationService.userMigration(externalId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Check service", description = "Provides check service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return HttpStatus",
                    content = {@Content(mediaType = "application/json", schema = @Schema())})})
    @GetMapping("check")
    public ResponseEntity<HttpStatus> checkService() {
        return ResponseEntity.ok().build();
    }

}
