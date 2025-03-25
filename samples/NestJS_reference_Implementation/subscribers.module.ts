import { Module } from '@nestjs/common';
import { SubscribersController } from './controllers/subscribers.controller';
import { SubscribersDbService } from './data/subscribers-db.service';
import { SubscribersHandler } from './handlers/subscribers.handler';
import { SharedModule } from '../shared/shared.module';

@Module({
    controllers: [SubscribersController],
    providers: [
        SubscribersDbService,
        SubscribersHandler
    ], // Provide the service and handler
    exports: [], // Export the services for use in other modules
    imports: [SharedModule],
})

export class SubscribersModule {}