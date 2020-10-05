# Exámen Mercado Libre
El presente proyecto es la resolución del exámen de Mutantes. El proyecto está realizado en Java (IDE Eclipse) utilizando el SDK de Amazon Web Services para la comunicación contra la base de datos NoSQL DynamoDB. El proyecto implementa los handlers de los servicios POST/mutant y GET/stats expuestos en el URL compartida. 
## Compilación
Para compilar el proyecto es necesario agregar el sitio de AWS para la instalación del SDK. Para hacerlo, en el menú *Ayuda -> Instalar nuevo software* agregar la dirección **https://aws.amazon.com/eclipse** en el campo **Trabajar con** y buscar el siguiente paquete:
> AWS Core Management Tools
## Invocación de los servicios
Para la invocación a los servicios es necesaria una aplicación que consuma servicios rest. Para las pruebas se ha utilizado ***Postman***
### mutant
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

### stats
Crear un nuevo servicio GET y colocar
>***URL***: *URL Enviada por mail*/stats

Presionar sobre *Send* para enviar el servicio que responderá el siguiente JSON con las estadísticas obtenidas hasta el momento. Por ejemplo:
> "{\"count_mutant_dna\":7,\"count_human_dna\":12,\"ratio\":0.58}"

