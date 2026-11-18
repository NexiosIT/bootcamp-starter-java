---
paths:
  - "src/test/**/*.java"
---
- JUnit 5 + AssertJ, naam: `shouldX_whenY`
- Arrange / Act / Assert, gescheiden door lege lijn
- Mockito (Java) / NSubstitute (.NET) voor dependencies zoals services en repositories, geen echte DB
- Controllertests: `@WebMvcTest` met `MockMvcTester`, service gemockt met `@MockitoBean`
