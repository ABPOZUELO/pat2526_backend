package edu.comillas.icai.gitt.pat.spring.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/carritos")
public class CarritoController {

    private final List<Carrito> storage = new ArrayList<>();

    @GetMapping
    public List<Carrito> getAll() {
        return storage;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Carrito create(@RequestBody Carrito carrito) {
        storage.add(carrito);
        return carrito;
    }

    @GetMapping("/{id}")
    public Carrito getById(@PathVariable Long id) {
        return storage.stream()
                .filter(c -> c.getIdCarrito().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public Carrito update(@PathVariable Long id, @RequestBody Carrito carritoActualizado) {
        Carrito carritoExistente = storage.stream()
                .filter(c -> c.getIdCarrito().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Actualizamos los campos
        carritoExistente.setIdArticulo(carritoActualizado.getIdArticulo());
        carritoExistente.setDescripcion(carritoActualizado.getDescripcion());
        carritoExistente.setUnidades(carritoActualizado.getUnidades());
        carritoExistente.setPrecioFinal(carritoActualizado.getPrecioFinal());

        return carritoExistente;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        boolean removed = storage.removeIf(c -> c.getIdCarrito().equals(id));
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}