import { BaseController } from '@mvc/controllers/base.controller';
import { Controller } from '@nestjs/common';
import { SubscribersHandler } from '../handlers/subscribers.handler';
import { SubscriberRequest } from '../http/requests/subscriber.request';
import { SubscriberDto } from '../dtos/subscriber.dto';
import { SubscribersListResponse } from '../http/responses/subscribers-list.response';
import { SubscriberResponse } from '../http/responses/subscriber.response';

@Controller('subscribers')
export class SubscribersController extends BaseController<
    SubscribersHandler,
    SubscriberRequest,
    SubscriberDto,
    SubscriberResponse,
    SubscribersListResponse,
    SubscriberDto
    > {
    constructor(handler: SubscribersHandler) {
        super(handler, SubscriberDto, SubscriberResponse, SubscribersListResponse);
    }

}
