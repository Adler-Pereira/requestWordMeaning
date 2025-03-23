package api.requestWordMeaning;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")

public class RequestWordMeaningControlller {
    private final RequestWordMeaning requestWordMeaning = new RequestWordMeaning();

    @GetMapping("/meaning-request")
    public Map<String, Object> requestMeaning(@RequestParam String word){
        return requestWordMeaning.requestMeaning(word);
    }
}
