import { Injectable } from '@nestjs/common';
import { BaseHandler } from '@mvc/handlers/base.handler';
import { SubscribersDbService } from '../data/subscribers-db.service';
import { SubscriberDto } from '../dtos/subscriber.dto';

@Injectable()
export class SubscribersHandler extends BaseHandler<SubscribersDbService, SubscriberDto, SubscriberDto> {
    constructor(dbService: SubscribersDbService) {
        super(dbService);
    }
}
