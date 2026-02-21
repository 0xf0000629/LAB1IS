package app.controller;

//import app.services.MinioService;
//import app.services.MinioService;
import app.services.*;
import app.appentities.*;
        import jakarta.servlet.http.HttpServletRequest;
        import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

        import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    @Autowired
    private final CityService cityService;

    public CityController() {
        cityService = new CityService(); // Instantiate the DAO
    }

    // this catches requests for the list of cities
    @GetMapping
    public List<City> getAllCitiesRequest() {
        return cityService.getAllCities();
    }

    // this catches requests for a city by its ID
    @GetMapping("/{id}")
    public City getCityByIdRequest(@PathVariable("id") Long id) {
        return cityService.getCityById(id);
    }

    // this catches requests for adding a city
    @PostMapping
    public City addCityRequest(HttpServletRequest request, @RequestBody City city) {
        String username = cityService.getUsername(request);
        return cityService.addCity(city, username);
    }

    // this catches requests for updating a city by ID
    @PutMapping("/{id}")
    public City updateCityRequest(HttpServletRequest request, @PathVariable("id") Long id, @RequestBody City updatedCity) {
        String username = cityService.getUsername(request);
        return cityService.updateCity(id, updatedCity, username);
    }

    // this catches requests for deleting a city by ID
    @DeleteMapping("/{id}")
    public String deleteCityRequest(HttpServletRequest request, @PathVariable("id") Long id) {
        String username = cityService.getUsername(request);
        return cityService.deleteCity(id, username);
    }

    // this catches requests for the average MASL function
    @GetMapping("/avrgMASL")
    public Double getavrgMASLRequest() {
        return cityService.getavrgMASL();
    }

    // this catches requests for the minimum standard of living function
    @GetMapping("/minSOL")
    public List<String> getCitiesWithSOLRequest(@RequestParam("minSOL") String minSOL) {
        System.out.println("GOT " + minSOL);
        return cityService.getCitiesWithSOL(minSOL);
    }

    // this catches requests for the cities with standard of living above X function
    @GetMapping("/uniqueC")
    public List<String> getCitiesWithClimateRequest() {
        return cityService.getCitiesWithClimate();
    }

    // this catches requests for population transfer function
    @GetMapping("/toanother")
    public void transferToAnotherRequest(@RequestParam("id1") int id1, @RequestParam("id2") int id2) {
        System.out.println("GOT " + id1 + " " + id2);
        cityService.transfertoanother(id1, id2);
    }

    // this catches requests for population transfer to smallest city function
    @GetMapping("/tosmallest")
    public void transferToSmallestRequest(@RequestParam("id1") int id1) {
        System.out.println("GOT " + id1);
        cityService.transfertosmallest(id1);
    }

    // this catches requests for mass city addition function
    @PostMapping("/mass")
    public ResponseEntity<Map<String, List <Long> >> massAdditionRequest(HttpServletRequest request, @RequestParam("file") MultipartFile file){
        String username = cityService.getUsername(request);
        return cityService.massAddition(file, username);
    }
}
