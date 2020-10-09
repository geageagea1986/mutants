# Examen Mercado Libre
El presente proyecto es la resolución del exámen de Mutantes. El proyecto está realizado en Java (IDE Eclipse) utilizando el SDK de Amazon Web Services para la comunicación contra la base de datos NoSQL DynamoDB. El proyecto implementa los handlers de los servicios POST/mutant y GET/stats expuestos en el URL compartida. 
## Compilación
Para compilar el proyecto y correr los test unit es necesario agregar el sitio de AWS para la instalación del SDK. Para hacerlo, en el menú *Ayuda -> Instalar nuevo software* agregar la dirección **https://aws.amazon.com/eclipse** en el campo **Trabajar con** y buscar los siguientes paquetes:
> AWS Core Management Tools

> AWS Deployment Tools	

> AWS Developer Tools	

> Optional - AWS Data Management Tools	
### Credenciales
Al instalar el paquete *AWS Core Management Tools* pedirá las claves para poder utilizar el ToolKit. Para los efectos de poder compilar y probar el proyecto se puede ingresar cualquier valor. Por ejemplo:
> AWS Access Key ID: "fakeMyKeyId"

> AWS Secret Access Key: "fakeSecretAccessKey"
## Tests Units
En el proyecto hay 2 clases para realizar Test Unit:
1. *MutantTest*: Hace pruebas con distintas cadenas para probar el código
2. *LambdaFunctionHandlerTest*: Prueba los handlers de los servicios expuestos (POST/mutant y GET/stats) con distintas variables.
Ambos TestUnits se deben correr con JUnit 5
## Base de datos
Para poder correr exitosamente la clase LambdaFunctionHandlerTest es necesario tener una base de datos local. Para ello, descomprimir el archivo *dynamodb_local_latest.tar.gz* en aglún directorio y ejecutar sobre una consola el siguiente comando:

    java -D"java.library.path=./DynamoDBLocal_lib" -jar DynamoDBLocal.jar

Por defecto el puerto que usa la base de datos es el 8000. Si no está disponible utilizar el argumento `-port` con el valor del puerto deseado y modificar la línea 46 del archivo `src\test\java\ejercicio\LambdaFunctionHandlerTest.java`
## Invocación de los servicios
Para la invocación a los servicios es necesaria una aplicación que consuma servicios rest. Para las pruebas se ha utilizado ***Postman***
### POST/mutant
Crear un nuevo servicio POST y colocar
>***URL***: *URL Enviada por mail*/mutant

En el tab de **Headers** agregar
>**Key**: Content-Type 

>**Value**: application/json

En el **Body**, seleccionar *raw* en el formato y colocar:

>{
    "dna": [
        "ACAA",
        "GTAG",
        "AAAA",
        "ATAA"
    ]
}

Presionar en *Send* para probar el servicio que responderá:
>"200 OK"

Reemplazar ***dna*** por la cadena a verificar

### GET/stats
Crear un nuevo servicio GET y colocar
>***URL***: *URL Enviada por mail*/stats

Presionar sobre *Send* para enviar el servicio que responderá el siguiente JSON con las estadísticas obtenidas hasta el momento. Por ejemplo:
> "{\"count_mutant_dna\":7,\"count_human_dna\":12,\"ratio\":0.58}"

