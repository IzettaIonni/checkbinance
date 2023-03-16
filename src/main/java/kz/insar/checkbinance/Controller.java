package kz.insar.checkbinance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
GET /test/hello
public class Controller {
    @Autowired
    private TextRepository repository;
    @GetMapping("/hello")
    public String test() {
        return repository.get_hello();
    }

}
