# Covid19UpdateSpain

Sistema para obtener notificaciones SMS ante actualizaciones de la web del ministerio que mantiene las estadisticas del estado del CodVid-19 en Espa�a


La arquitectura Aws consistir� de los siguientes elementos:
	- Bucket privado: donde se guardar� el ultimo estado de la web del ministerio.
	- CloudWatch -Eventbride: scheduler que se ejecutar� cada 5 minutos.
	- Lambda: funci�n desarrollada en Java 1.8 encargada de comprobar si hay novedades en la web del ministerio, e invocar el servicio AWS-SNS.
	- SNS: Sistema encargado de enviar las notificaciones mediante SMS.

### Pasos a seguir:


```
1) Creacci�n de un bucket privado donde se almacenar�:
	- Jar con proyecto java que se desplegara como funci�n Lambda.
	- Copia de la web y datos con las ultimas estadisticas de la web del ministererio del estado del Codvid-10 en Espa�a. Como inicio se generar�n los ficheros : current_web.txt y  resources/data.csv que contendran la copia de la web. 
		Adiccionalmente se crear� el fichero con el listado de n�meros telefonicos donde enviar las notificaciones.
2) Importar el proyecto java que se encuentra en xxx y generar el jar de despliegue:
	- El proyecto java se encarga de comparar la web del ministerio con una copia que se encuentra en el bucket.
	- Si en cuentra diferencias, guardar� la web en el bucket, y enviar� una notificaci�n SMS a la lista de telefonos.
3) Tras ello se crear� la funci�n Lambda:
	a) Se elije como Runtime Java 1.8
	b) Se utilizar� el asistente de Lambda para crear el rol que ejecutar� funci�n, y para el cual habr� que a�adir dos permisos adiccionales de IAM: S3FullAccess y SNSFullAccess.
	c) Se indica la ruta de s3 donde se ha subido el jar con la aplicaci�n.
	d) Se creara la propiedad "BUCKET_NAME" que har� referencia al bucket creado anteriormente, y que guardar� una copia de la web.
	e) En este punto se podr� ejecutar un test manual de la funci�n lambda, para comprobar que se actualiza la web en el bucket y se envia las notificaciones SMS.
4) Una vez comprobado el resultado de la funci�n Lambda, se proceder� a crear un Trigger para que se ejecute cada 5 minutos:
	a) Se pulsa en a�adir Trigger.
	b) Se selecciona CloudWatch Events/EventBridge
	c) En schedule expression se indica: rate(5 minutes)

```
