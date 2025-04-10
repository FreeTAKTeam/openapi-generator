//
// AnotherFakeAPI.swift
//
// Generated by openapi-generator
// https://openapi-generator.tech
//

import Foundation
@preconcurrency import PromiseKit

open class AnotherFakeAPI {

    /**
     To test special tags
     
     - parameter body: (body) client model 
     - parameter apiConfiguration: The configuration for the http request.
     - returns: Promise<Client>
     */
    open class func call123testSpecialTags(body: Client, apiConfiguration: PetstoreClientAPIConfiguration = PetstoreClientAPIConfiguration.shared) -> Promise<Client> {
        let deferred = Promise<Client>.pending()
        call123testSpecialTagsWithRequestBuilder(body: body, apiConfiguration: apiConfiguration).execute { result in
            switch result {
            case let .success(response):
                deferred.resolver.fulfill(response.body)
            case let .failure(error):
                deferred.resolver.reject(error)
            }
        }
        return deferred.promise
    }

    /**
     To test special tags
     - PATCH /another-fake/dummy
     - To test special tags and operation ID starting with number
     - parameter body: (body) client model 
     - parameter apiConfiguration: The configuration for the http request.
     - returns: RequestBuilder<Client> 
     */
    open class func call123testSpecialTagsWithRequestBuilder(body: Client, apiConfiguration: PetstoreClientAPIConfiguration = PetstoreClientAPIConfiguration.shared) -> RequestBuilder<Client> {
        let localVariablePath = "/another-fake/dummy"
        let localVariableURLString = apiConfiguration.basePath + localVariablePath
        let localVariableParameters = JSONEncodingHelper.encodingParameters(forEncodableObject: body, codableHelper: apiConfiguration.codableHelper)

        let localVariableUrlComponents = URLComponents(string: localVariableURLString)

        let localVariableNillableHeaders: [String: (any Sendable)?] = [
            "Content-Type": "application/json",
        ]

        let localVariableHeaderParameters = APIHelper.rejectNilHeaders(localVariableNillableHeaders)

        let localVariableRequestBuilder: RequestBuilder<Client>.Type = apiConfiguration.requestBuilderFactory.getBuilder()

        return localVariableRequestBuilder.init(method: "PATCH", URLString: (localVariableUrlComponents?.string ?? localVariableURLString), parameters: localVariableParameters, headers: localVariableHeaderParameters, requiresAuthentication: false, apiConfiguration: apiConfiguration)
    }
}
