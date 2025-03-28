
import { HttpResponse } from "@mvc/http/responses/http.response";
import { SubscriberDto } from '../../dtos/subscriber.dto';

export class SubscriberResponse extends HttpResponse {
    constructor(data: SubscriberDto, details?: object) {
        super(data, 'Subscriber', data.id, details);
    }
}

