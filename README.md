# 🧡 Merendero Fácil

**Merendero Fácil** es una plataforma web que permite a las personas:  
- Donar a distintos merenderos, mediante un mapa interactivo.  
- Exponer sus merenderos para recibir donaciones y tener un mejor control de donaciones y stock.  

### Tecnologías utilizadas
**Frontend:** Angular 18+  
**Backend:** Java 17, Spring Boot 3  
**Base de Datos:** MySql  

### Recursos y prácticas aplicadas:  

### Backend
. Arquitectura basada en **microservicios** desarrollados con **Spring Boot 3**.  

. Implementación de **Spring Security y JWT** para autenticación y autorización con control de roles.  

. Integración con Mercado Pago mediante:  
- Checkout Pro para procesamiento de donaciones  
- Marektplace para que cada merendero reciba donaciones en su propia cuenta  

. Notificaciones por email para recuperación de contraseña y stock bajo

. Uso de **DTOs** para transferencia segura de datos entre capas y clases Mapper **dedicadas** para conversión eficiente entre entidades.

. Sistemas de reportes con:  
- Dashboard de estadísticas
- Agrupación de datos flexible (día, semana, mes)
- Análisis comparativo entre períodos

### Frontend

. Angular 18+ con componentes standalone y arquitectura modular

. Google Maps API integrada para mapa interactivo con marcadores y ventanas de información 

. Diseño responsive con CSS personalizado y Bootstrap para múltiples dispositivos  

. Formularios reactivos con validación síncrona y asíncrona en tiempo real  

. Dashboard interactivo con gráficos responsive (Google Charts)  


> Este proyecto fue desarrollado como parte de la Práctica Supervisada de la carrera Tecnicatura Universitaria en Programación en la Universidad Tecnológica Nacional.
