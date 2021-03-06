package com.udacity.vehicles.api;

import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Implements a REST-based controller for the Vehicles API.
 */
@RestController
@RequestMapping("/cars")
class CarController {

    private final CarService carService;
    private final CarResourceAssembler assembler;

    CarController(CarService carService, CarResourceAssembler assembler) {
        this.carService = carService;
        this.assembler = assembler;
    }

    /**
     * Creates a list to store any vehicles.
     *
     * @return list of vehicles
     */
    @Operation(summary = "Retrieve all Vehicles")
    @ApiResponse(
            responseCode = "200",
            description = "Found all Vehicles"
    )
    @GetMapping
    CollectionModel<EntityModel<Car>> list() {
        List<EntityModel<Car>> resources = carService.list().stream().map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(resources,
                linkTo(methodOn(CarController.class).list()).withSelfRel());
    }

    /**
     * Gets information of a specific car by ID.
     *
     * @param id the id number of the given vehicle
     * @return all information for the requested vehicle
     */
    @Operation(
            summary = "Retrieve a Vehicle",
            description = "This feature retrieves the Vehicle data from the database and access the Pricing Service " +
                    "and Boogle Maps to enrich the Vehicle information to be presented")
    @ApiResponse(
            responseCode = "200",
            description = "Found the Vehicle"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Vehicle not found",
            content = @Content
    )
    @GetMapping("/{id}")
    EntityModel<Car> get(@PathVariable Long id) {
        var car = carService.findById(id);
        return assembler.toModel(car);
    }

    /**
     * Posts information to create a new vehicle in the system.
     *
     * @param car A new vehicle to add to the system.
     * @return response that the new vehicle was added to the system
     * @throws URISyntaxException if the request contains invalid fields or syntax
     */
    @Operation(summary = "Create a Vehicle")
    @ApiResponse(
            responseCode = "201",
            description = "Created new Vehicle resource"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                    {
                                       "condition":"USED",
                                       "details":{
                                          "body":"sedan",
                                          "model":"Impala",
                                          "manufacturer":{
                                             "code":101,
                                             "name":"Chevrolet"
                                          },
                                          "numberOfDoors":4,
                                          "fuelType":"Gasoline",
                                          "engine":"3.6L V6",
                                          "mileage":32280,
                                          "modelYear":2018,
                                          "productionYear":2018,
                                          "externalColor":"white"
                                       },
                                       "location":{
                                          "lat":40.73061,
                                          "lon":-73.935242
                                       }
                                    }
                                    """
                    )
            )
    )
    @PostMapping
    ResponseEntity<EntityModel<Car>> post(@Valid @RequestBody Car car) throws URISyntaxException {
        var savedCar = carService.save(car);
        var model = assembler.toModel(savedCar);
        return ResponseEntity.created(new URI(model.getRequiredLink("self").getHref())).body(model);
    }

    /**
     * Updates the information of a vehicle in the system.
     *
     * @param id  The ID number for which to update vehicle information.
     * @param car The updated information about the related vehicle.
     * @return response that the vehicle was updated in the system
     */
    @Operation(summary = "Update a Vehicle")
    @ApiResponse(
            responseCode = "200",
            description = "Updated the Vehicle"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Vehicle not found",
            content = @Content
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                    {
                                        "condition":"USED",
                                        "details":{
                                           "body":"sedan",
                                           "model":"Impala",
                                           "manufacturer":{
                                              "code":101,
                                              "name":"Chevrolet"
                                           },
                                           "numberOfDoors":4,
                                           "fuelType":"Gasoline",
                                           "engine":"3.6L V6",
                                           "mileage":32280,
                                           "modelYear":2018,
                                           "productionYear":2018,
                                           "externalColor":"white"
                                        },
                                        "location":{
                                           "lat":40.73061,
                                           "lon":-73.935242
                                        }
                                     }
                                    """)))
    @PutMapping("/{id}")
    ResponseEntity<EntityModel<Car>> put(@PathVariable Long id, @Valid @RequestBody Car car) {
        car.setId(id);
        var savedCar = carService.save(car);
        EntityModel<Car> resource = assembler.toModel(savedCar);
        return ResponseEntity.ok(resource);
    }

    /**
     * Removes a vehicle from the system.
     *
     * @param id The ID number of the vehicle to remove.
     * @return response that the related vehicle is no longer in the system
     */
    @Operation(summary = "Delete a Vehicle")
    @ApiResponse(
            responseCode = "204",
            description = "Deleted the Vehicle"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Vehicle not found",
            content = @Content
    )

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        carService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
