package com.e207.woojoobook.api.extension;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.e207.woojoobook.api.extension.request.ExtensionRespondRequest;

@RequiredArgsConstructor
@RestController
public class ExtensionController {

    private final ExtensionService extensionService;

    @PostMapping("/rentals/{rentalId}/extensions")
    public ResponseEntity<?> extension(@PathVariable("rentalId") Long rentalId) {
        this.extensionService.extensionRental(rentalId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/extensions/{extensionId}")
    public ResponseEntity<?> extensionRespond(@PathVariable("extensionId") Long extensionId,
        @RequestBody ExtensionRespondRequest request) {
        this.extensionService.respond(extensionId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/extensions/{extensionId}")
    public ResponseEntity<?> extensionDelete(@PathVariable("extensionId") Long extensionId) {
        this.extensionService.delete(extensionId);
        return ResponseEntity.ok().build();
    }
}
