import { Injectable } from '@nestjs/common';
import { BaseDbService } from '@mvc/data/base-db.service';
import { Prisma } from "@prisma/client";
import { PrismaService } from "@prisma";
import { SubscriberDto } from '../dtos/subscriber.dto';
import { SubscriberNotFoundError } from '../exceptions/subscriber-not-found-error';

@Injectable()
export class SubscribersDbService extends BaseDbService<Prisma.SubscribersDelegate, SubscriberDto, SubscriberDto> {
    constructor(prisma: PrismaService) {
        super(SubscriberDto, prisma.subscribers);
    }

    throw404(id: string): never {
        throw new SubscriberNotFoundError(id);
    }
}