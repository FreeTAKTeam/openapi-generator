# OpenAPI Generator Commands

## Generate NestJS TypeScript Code

Use the following command to generate NestJS TypeScript code from your OpenAPI specification:

```bash
rm -rf viewbid-nestjs/output && \
java -jar modules/openapi-generator-cli/target/openapi-generator-cli.jar generate \
    -i viewbid-nestjs/yamls/SubscriberManagementAPI-OAS.yaml \
    -g typescript-nestjs \
    -o viewbid-nestjs/output \
    -c viewbid-nestjs/config.yml \
    --skip-validate-spec
```

### Command Breakdown:
- Removes existing output directory
- Uses OpenAPI Generator CLI
- Inputs the Subscriber Management API spec
- Generates TypeScript NestJS code
- Outputs to specified directory
- Uses custom configuration
- Skips spec validation
