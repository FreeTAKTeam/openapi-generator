import { IsNotEmpty, IsString, IsBoolean, IsOptional } from 'class-validator';

export class SubscriberRequest {

    @IsOptional()
    @IsString()
    id: string;

    @IsOptional()
    @IsString()
    accountMappingsId: string;

    @IsOptional()
    @IsString()
    subscriptionKey: string;
}
