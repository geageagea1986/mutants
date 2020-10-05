# Exámen Mercado Libre
El presente proyecto es la resolución del exámen de Mutantes. El proyecto está realizado en Java (IDE Eclipse) utilizando el SDK de Amazon Web Services para la comunicación contra la base de datos DynamoDB. El proyecto implementa los handlers de los servicios POST/mutant y GET/stats expuestos en el URL compartida. 
## Compilación
Para compilar el proyecto es necesario agregar el sitio de AWS para la instalación del SDK. Para hacerlo, en el menú *Ayuda -> Instalar nuevo software* agregar la dirección **https://aws.amazon.com/eclipse** en el campo **Trabajar con** y buscar el siguiente paquete:
> AWS Core Management Tools
## Invocación de los servicios
Para la invocación a los servicios es necesaria una aplicación que consuma servicios rest. Para las pruebas se ha utilizado ***Postman***
### mutant
Crear un nuevo servicio POST y colocar en la URL
>***URL Enviada por mail*/mutant**
En el tab de Headers agregar 
>Key: Content-Type 
>Value: application/json
