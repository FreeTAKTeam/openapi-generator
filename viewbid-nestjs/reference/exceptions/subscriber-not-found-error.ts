import { NotFoundSystemError } from '@mvc/exceptions/not-found-system.error';
import { Constants } from  '../config/constants';

export class SubscriberNotFoundError extends NotFoundSystemError {
    constructor(id: string) {
        super('No Subscriber found with id ' + id, Constants.SUBSCRIBER_NOT_FOUND_ERROR, id, 'subscriber#not-found');
    }
}


