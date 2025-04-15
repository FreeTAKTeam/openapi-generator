## Key Features
###  OpenAPI-Driven
- Fully compliant with OpenAPI 3.0/3.1 specifications
- Generates clean, idiomatic NestJS controllers, services, DTOs, and routing modules

### AI Code Generation
- Automatically generates method bodies for controller and service methods using GPT-4.1
- Supports inline business logic implementation based on operation descriptions
- Leverages structured metadata from OpenAPI (including summary, description, parameters, and responses)

### Secure by Design
 - Encourages the use libraries and best practices (e.g., Node.js crypto, exception handling)
 - Avoids placeholder logic and boilerplate by default
- Configurable guardrails (e.g., delegation control, prompt injection rules)

### Highly Configurable
 - Supports temperature, max tokens, top-p, and penalty parameters
 - Easily extended with custom system prompts and user prompt templates
 - ChatGPT API key injected via environment variable (LLM_API_KEY)

## How It Works
- Define your API using OpenAPI 3.x (YAML ) or generate it using the [DAF OAS Generator](https://github.com/FreeTAKTeam/DAF_OAS_Generator)
- Include meaningful summary and description fields in operations


# OpenAPI Generator Commands

## Generate NestJS TypeScript Code

Use the following command to generate Levios NestJS TypeScript code from your OpenAPI specification:

set a key for the LLM
```
set LLM_API_Key=[ChatGPTAPIKEY]
```


```bash

rm -rf viewbid-nestjs/output && \
java -jar modules/openapi-generator-cli/target/openapi-generator-cli.jar generate -c viewbid-nestjs/config.yml
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
