# Lab 3 Complete a Web API -- Project Report

## Description of Changes
En esta práctica han sido completados los test unitarios del archivo ControllerTests.kt para comprobar y asegurar
el correcto comportamiento de la API REST de empleados EmployeeController. 

 <ins>**Cambios realizados**</ins>
 
    - Implementación completa de 4 tests HTTP:
        + Test POST: Comprueba que POST no es seguro ni idempotente
        + Test GET: Comprueba que GET es seguro e idempotente
        + Test PUT: Comprueba que PUT es idempotente pero no seguro
        + Test DELETE: Verifica que DELETE es idempotente pero no seguro

    - Configuración de Mocks(SETUP):
        + Configuración mocks del EmployeeRepository mediante el uso de MockK
        + Simulación de distintas situaciones: recursos existentes, no existentes, creación y actualización de recursos.

    - Verificaciones(VERIFY):
        + Verificación de las llamadas exactas a los métodos existentes en el repositorio
        + Verificación de que los métodos seguros (GET) no modifican el estado
        + Verificación del correcto comportamiento GET, PUT y DELETE
        + Se ha podido corroborar que POST crea recursos con ID's diferentes en cada llamada


## Technical Decisions
[Explanation of technical choices made]

## Learning Outcomes
[What you learned from this assignment]

## AI Disclosure
### AI Tools Used
- [List specific AI tools used]

### AI-Assisted Work
- [Describe what was generated with AI assistance]
- [Percentage of AI-assisted vs. original work]
- [Any modifications made to AI-generated code]

### Original Work
- [Describe work done without AI assistance]
- [Your understanding and learning process]