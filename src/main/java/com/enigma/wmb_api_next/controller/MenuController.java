package com.enigma.wmb_api_next.controller;

import com.enigma.wmb_api_next.constant.ApiUrl;
import com.enigma.wmb_api_next.constant.StatusMessage;
import com.enigma.wmb_api_next.dto.request.MenuRequest;
import com.enigma.wmb_api_next.dto.request.SearchMenuRequest;
import com.enigma.wmb_api_next.dto.request.UpdateMenuRequest;
import com.enigma.wmb_api_next.dto.response.CommonResponse;
import com.enigma.wmb_api_next.dto.response.MenuResponse;
import com.enigma.wmb_api_next.dto.response.PaginationResponse;
import com.enigma.wmb_api_next.service.MenuService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiUrl.API_URL + ApiUrl.MENU_URL)
public class MenuController {
    private final MenuService menuService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "Save new menu")
    @SecurityRequirement(name = "Authorization")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<CommonResponse<?>> save(
            @RequestPart(name = "menu") String menu,
            @RequestPart(name = "image") @Nullable MultipartFile image
    ) {
        try {
            MenuRequest menuRequest = objectMapper.readValue(menu, new TypeReference<>() {
            });
            menuRequest.setImage(image);

            MenuResponse saved = menuService.save(menuRequest);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(getCommonResponse(saved, HttpStatus.CREATED, StatusMessage.SUCCESS_CREATE));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(getCommonResponse(null, HttpStatus.INTERNAL_SERVER_ERROR, StatusMessage.INTERNAL_SERVER_ERROR));
        }
    }

    @Operation(summary = "Save new menu in bulk")
    @SecurityRequirement(name = "Authorization")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping(
            path = "/bulk",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<List<MenuResponse>>> saveBulk(@RequestBody List<MenuRequest> menu) {
        List<MenuResponse> menuResponses = menuService.saveBulk(menu);
        return ResponseEntity.status(HttpStatus.CREATED).body(getCommonResponseList(menuResponses));
    }

    @Operation(summary = "Get menu by id")
    @SecurityRequirement(name = "Authorization")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<MenuResponse>> getById(@PathVariable String id) {
        MenuResponse response = menuService.getById(id);
        return ResponseEntity.ok(getCommonResponse(response, HttpStatus.OK, StatusMessage.SUCCESS_RETRIEVE));
    }

    @Operation(summary = "Get all menu")
    @SecurityRequirement(name = "Authorization")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<List<MenuResponse>>> getAll(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "minPrice", required = false) Long minPrice,
            @RequestParam(name = "maxPrice", required = false) Long maxPrice,
            @RequestParam(name = "direction", defaultValue = "asc") String direction,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        SearchMenuRequest searchMenuRequest = SearchMenuRequest.builder()
                .name(name)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .direction(direction)
                .sortBy(sortBy)
                .page(page)
                .size(size)
                .build();
        Page<MenuResponse> menuResponses = menuService.getAll(searchMenuRequest);

        PaginationResponse paginationResponse = PaginationResponse.builder()
                .totalPages(menuResponses.getTotalPages())
                .totalElement(menuResponses.getTotalElements())
                .page(menuResponses.getNumber() + 1)
                .size(menuResponses.getSize())
                .hasPrevious(menuResponses.hasPrevious())
                .hasNext(menuResponses.hasNext())
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<List<MenuResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(StatusMessage.SUCCESS_RETRIEVE_LIST)
                        .data(menuResponses.getContent())
                        .paginationResponse(paginationResponse)
                        .build());
    }

    @Operation(summary = "Update menu")
    @SecurityRequirement(name = "Authorization")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PutMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<CommonResponse<MenuResponse>> update(
            @RequestPart(name = "menu") String menu,
            @RequestPart(name = "image") @Nullable MultipartFile image
    ) {
        try {
            UpdateMenuRequest updateMenuRequest = objectMapper.readValue(menu, new TypeReference<>() {
            });
            updateMenuRequest.setImage(image);
            MenuResponse updated = menuService.update(updateMenuRequest);
            return ResponseEntity.ok(getCommonResponse(updated, HttpStatus.OK, StatusMessage.SUCCESS_UPDATE));
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(getCommonResponse(null, HttpStatus.INTERNAL_SERVER_ERROR, StatusMessage.INTERNAL_SERVER_ERROR));
        }
    }

    @Operation(summary = "Delete menu")
    @SecurityRequirement(name = "Authorization")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @DeleteMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<String>> delete(@PathVariable String id) {
        menuService.delete(id);
        CommonResponse<String> response = CommonResponse.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message(StatusMessage.SUCCESS_DELETE)
                .build();
        return ResponseEntity.ok(response);
    }

    private CommonResponse<MenuResponse> getCommonResponse(MenuResponse menuResponse, HttpStatus status, String message) {
        return CommonResponse.<MenuResponse>builder()
                .statusCode(status.value())
                .message(message)
                .data(menuResponse)
                .build();
    }

    private CommonResponse<List<MenuResponse>> getCommonResponseList(List<MenuResponse> menuResponses) {
        return CommonResponse.<List<MenuResponse>>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(StatusMessage.SUCCESS_CREATE_LIST)
                .data(menuResponses)
                .build();
    }

}
