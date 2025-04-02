# OpenAPI Generator Commands

## Generate NestJS TypeScript Code

Use the following command to generate Levios NestJS TypeScript code from your OpenAPI specification:

set a key for the LLM
```
set LLM_API_Key=[ChatGPTAPIKEY]
```


```bash

rm -rf viewbid-nestjs/output && \
java -jar modules/openapi-generator-cli/target/openapi-generator-cli.jar generate \
    -i viewbid-nestjs/yamls/SubscriberManagementAPI-OAS.yaml \
    -g typescript-nestjs \
    -o viewbid-nestjs/output \
    -c viewbid-nestjs/config.yml \
    --skip-validate-spec
```

``` cmd
rd /s /q "viewbid-nestjs\output" ^
java -jar viewbid-nestjs/openapi-generator-cli.jar generate ^
    -i viewbid-nestjs/yamls/LoanManagementAPI.yaml ^
    -g typescript-nestjs ^
    -o viewbid-nestjs/output ^
    -c viewbid-nestjs/config.yml ^
    --skip-validate-spec
```


### Command Breakdown:
- Removes existing output directory
- Uses OpenAPI Generator CLI
- Inputs  API spec
- Generates TypeScript NestJS code
- Outputs to specified directory
- Uses custom configuration
- Skips spec validation
