package com.example.gestionmateriel;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    AuthUtilsTest.class,
    CloudinaryConfigTest.class,
    FeignTokenInterceptorTest.class,
    MaterielTest.class
})
public class AllServiceTests {
}
