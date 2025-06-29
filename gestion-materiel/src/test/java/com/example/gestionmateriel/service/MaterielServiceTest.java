package com.example.gestionmateriel.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.example.gestionmateriel.model.Materiel;
import com.example.gestionmateriel.repository.MaterielRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MaterielServiceTest {

    @Mock private MaterielRepository materielRepository;
    @Mock private Cloudinary cloudinary;
    @Mock private Uploader uploader;
    @Mock private MultipartFile mockFile;

    @InjectMocks private MaterielService materielService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(cloudinary.uploader()).thenReturn(uploader);
    }

    @Test
    void testGetAllMateriel() {
        when(materielRepository.findAll()).thenReturn(List.of(new Materiel(), new Materiel()));
        assertEquals(2, materielService.getAllMateriel().size());
    }

    @Test
    void testGetAllMaterielPaginated() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Materiel> expectedPage = new PageImpl<>(List.of(new Materiel(), new Materiel()));
        when(materielRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<Materiel> result = materielService.getAllMaterielPaginated(pageable);
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
    }

    @Test
    void testGetMaterielByIdFound() {
        Materiel m = new Materiel(); m.setId("abc");
        when(materielRepository.findById("abc")).thenReturn(Optional.of(m));
        assertEquals("abc", materielService.getMaterielById("abc").getId());
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

    @Test
    void testSaveImage() throws Exception {
        when(mockFile.getBytes()).thenReturn("image".getBytes());
        when(uploader.upload(any(), any())).thenReturn(Map.of("secure_url", "https://mocked-url.com/image.png"));
        String result = materielService.saveImage(mockFile);
        assertEquals("https://mocked-url.com/image.png", result);
    }

    @Test
    void testCreateMaterielWithImage() throws Exception {
        Materiel materiel = new Materiel();
        when(mockFile.getBytes()).thenReturn("bytes".getBytes());
        when(mockFile.isEmpty()).thenReturn(false);
        when(uploader.upload(any(), any())).thenReturn(Map.of("secure_url", "http://mock-url.com/image.jpg"));
        when(materielRepository.save(any())).thenReturn(materiel);
        Materiel result = materielService.createMaterielWithImage(materiel, mockFile);
        assertNotNull(result);
    }

    @Test
    void testUpdateMaterielWithImage() throws Exception {
        Materiel materiel = new Materiel();
        when(mockFile.getBytes()).thenReturn("bytes".getBytes());
        when(uploader.upload(any(), any())).thenReturn(Map.of("secure_url", "http://mock-url.com/image.jpg"));
        when(materielRepository.save(any())).thenReturn(materiel);
        Materiel result = materielService.updateMaterielWithImage("id1", materiel, mockFile);
        assertNotNull(result);
    }

    @Test
    void testGetMaterielByIdOptional() {
        Materiel m = new Materiel(); m.setId("id");
        when(materielRepository.findById("id")).thenReturn(Optional.of(m));
        Optional<Materiel> result = materielService.getMaterielByIdOptional("id");
        assertTrue(result.isPresent());
        assertEquals("id", result.get().getId());
    }

    @Test
    void testUpdateMateriel() {
        Materiel m = new Materiel(); m.setName("Laptop");
        m.setId("mat-001");
        when(materielRepository.save(any())).thenReturn(m);
        Materiel updated = materielService.updateMateriel("mat-001", m);
        assertEquals("mat-001", updated.getId());
    }

    @Test
    void testGetAllMaterielPaginatedAndSearch_withSearchTerm() {
        Page<Materiel> page = new PageImpl<>(List.of(new Materiel()));
        when(materielRepository.searchAllFields(eq("laptop"), any())).thenReturn(page);
        Page<Materiel> result = materielService.getAllMaterielPaginatedAndSearch(PageRequest.of(0, 5), "laptop", null, null);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testGetAllMaterielPaginatedAndSearch_noSearchTerm() {
        Page<Materiel> page = new PageImpl<>(List.of(new Materiel()));
        Pageable pageable = PageRequest.of(0, 5);
        when(materielRepository.findAll(pageable)).thenReturn(page);
        Page<Materiel> result = materielService.getAllMaterielPaginatedAndSearch(pageable, "", null, null);
        assertEquals(1, result.getContent().size());
    }
}
