package com.example.gestionmateriel.service;

import com.example.gestionmateriel.model.Materiel;
import com.example.gestionmateriel.repository.MaterielRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MaterielServiceTest {

    @Mock
    private MaterielRepository materielRepository;

    @InjectMocks
    private MaterielService materielService;

    public MaterielServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllMateriel() {
        when(materielRepository.findAll()).thenReturn(List.of(new Materiel(), new Materiel()));
        List<Materiel> materiels = materielService.getAllMateriel();
        assertEquals(2, materiels.size());
    }

    @Test
    void testGetMaterielByIdFound() {
        Materiel m = new Materiel(); m.setId("abc");
        when(materielRepository.findById("abc")).thenReturn(Optional.of(m));
        Materiel result = materielService.getMaterielById("abc");
        assertNotNull(result);
        assertEquals("abc", result.getId());
    }

    @Test
    void testCreateMateriel() {
        Materiel m = new Materiel();
        when(materielRepository.save(m)).thenReturn(m);
        assertEquals(m, materielService.createMateriel(m));
    }

    @Test
    void testDeleteMateriel() {
        doNothing().when(materielRepository).deleteById("id");
        materielService.deleteMateriel("id");
        verify(materielRepository, times(1)).deleteById("id");
    }
}
