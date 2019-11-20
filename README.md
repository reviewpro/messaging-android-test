# Reviewpro Android test

Android app to test Android integrations from Smooch.

## Run

Only "Run app" in the play button.

## Params

### Mandatory

* app_id: The identifier of the app you can found in https://app.smooch.io


### Optional

If you want to perform authentication (to get old conversations from an userId), otherwise the conversation will be anonymous (always a new conversation).

```
* User Identifier: The identifier of the user (a string generated as you want, an email, a phone number, an email + a phone number)
* Key Id: The key id you can found in the Settings of your appId in https://app.smooch.io
* key Secret: The secret you can found in the Settings of your appId in https://app.smooch.io
```

```
* User properties: If you want to add properties to the user logged in (metadata to identify room for example)
```

## Predefine params

If you want to predefine params before run to avoid copy and paste ids in the mobile app, you only have to fill the values above in `values/strings.xml`:

### Mandatory

```
<string name="app_id_example"></string>
```

### Optional

```
<string name="key_id_example"></string>
<string name="key_secret_example"></string>
<string name="key_value_example"></string>
<string name="user_id_example"></string>
```