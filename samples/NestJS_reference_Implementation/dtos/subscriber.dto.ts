export class SubscriberDto {
    id: string;
    accountMappingsId: string;
    subscriptionKey: string;

    constructor(row: any) {
        this.id = row.id;
        this.accountMappingsId = row.accountMappingsId;
        this.subscriptionKey = row.subscriptionKey;
    }
}
