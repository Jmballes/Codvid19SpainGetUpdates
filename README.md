# Covid19UpdateSpain

Sistema para obtener notificaciones SMS ante actualizaciones de la web del ministerio que mantiene las estadisticas del estado del CodVid-19 en España


La arquitectura Aws consistirá de los siguientes elementos:

1) Bucket privado: donde se guardará el ultimo estado de la web del ministerio.
2) CloudWatch -Eventbride: scheduler que se ejecutará cada 5 minutos.
3) Lambda: función desarrollada en Java 1.8 encargada de comprobar si hay novedades en la web del ministerio, e invocar el servicio AWS-SNS.
4) SNS: Sistema encargado de enviar las notificaciones mediante SMS.

### Pasos a seguir:


```
1) Creacción de un bucket privado donde se almacenará:
	- Jar con proyecto java que se desplegara como función Lambda.
	- Copia de la web y datos con las ultimas estadisticas de la web del ministererio del estado del Codvid-10 en España. Como inicio se generarán los ficheros : current_web.txt y  resources/data.csv que contendran la copia de la web. 
		Adiccionalmente se creará el fichero con el listado de números telefonicos donde enviar las notificaciones.
2) Importar el proyecto java que se encuentra en xxx y generar el jar de despliegue:
	- El proyecto java se encarga de comparar la web del ministerio con una copia que se encuentra en el bucket.
	- Si en cuentra diferencias, guardará la web en el bucket, y enviará una notificación SMS a la lista de telefonos.
3) Tras ello se creará la función Lambda:
	a) Se elije como Runtime Java 1.8
	b) Se utilizará el asistente de Lambda para crear el rol que ejecutará función, y para el cual habrá que añadir dos permisos adiccionales de IAM: S3FullAccess y SNSFullAccess.
	c) Se indica la ruta de s3 donde se ha subido el jar con la aplicación.
	d) Se creara la propiedad "BUCKET_NAME" que hará referencia al bucket creado anteriormente, y que guardará una copia de la web.
	e) En este punto se podrá ejecutar un test manual de la función lambda, para comprobar que se actualiza la web en el bucket y se envia las notificaciones SMS.
4) Una vez comprobado el resultado de la función Lambda, se procederá a crear un Trigger para que se ejecute cada 5 minutos:
	a) Se pulsa en añadir Trigger.
	b) Se selecciona CloudWatch Events/EventBridge
	c) En schedule expression se indica: rate(5 minutes)

```
