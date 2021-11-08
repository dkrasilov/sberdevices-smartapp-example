require: slotfilling/slotFilling.sc
  module = sys.zb-common
  
theme: /

    state: Start
        q!: $regex</start>
        a:  Я покажу как пользоваться Push-уведомлениями. 
        buttons: 
            "Получить Push" -> /Send_push
        
    state: Push
        intent!: /push
        go!: /Send_push
        
    state: Send_push
        script: 
            var deliveryConfig = {
                deliveryMode:  'BROADCAST',
                surface: 'SALUT',
                templateContent: {
                    id: '175d575a-f49a-4e70-84ef-2173415f8f47', //Необходимо вставить id шаблона Push-уведомления
                    headerValues: { //Содержит переменные заголовка push-уведомления для подстановки в шаблон.
                    },
                    bodyValues: { //Содержит переменные тела push-уведомления для подстановки в шаблон.
                    },
                    mobileAppParameters: {
                        deeplinkAndroid: 'example-listen-android',
                        deeplinkIos: 'example-mai-listen-ios',
                        buttonText: 'Слушать'
                    }
                }
            };
            var authConfig = {
                client_id: "$credentials.smartServices.client_id", //идентификатор пользователя сервиса в виде строки, заданный с помощью переменной из раздела Токены.
                secret: "$credentials.smartServices.secret", //секрет пространства в виде строки, заданный с помощью переменной из раздела Токены.
                scope: "SMART_PUSH" //скоуп действия параметров авторизации в виде строки.
            }
            $smartPush.send(deliveryConfig, authConfig)
                    .then(function (success) {
                        $response.data = "Push отправлен";
                        $response.status = success.status;
                        $response.response = success.response;
                    })
                    .catch(function (error) {
                        $response.data = "Ошибка отправки Push-уведомления"
                        $response.status = error.status;
                        $response.response = error.response;
                        $response.error = error.error;
                    });
        a: {{$response.data}}
        go!: /Exit

    state: Fallback
        event!: noMatch
        a: Непонятно
        go!: /Start
        
    state: Exit
        intent!: /Exit
        a: До новых встреч!
        script:
            var reply = {
                type: "raw",
                body: {
                    "items": [
                    {
                      "command": {
                        "type": "close_app"
                      }
                    }]
                }
            };
            $response.replies = $response.replies || [];
            $response.replies.push(reply);
            $jsapi.stopSession();
        

