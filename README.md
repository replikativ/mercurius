# mercurius

This library constitutes a payment provider usable in combination with the augmented Eclipse license below. The general idea is to use licensing to enforce payments by designated user groups through function invocations in the code. All open-source freedoms are granted as usual, but if code contains payment calls that require the user to pay then it must be executed against a payment provider, such as the one provided by mercurius. The choice of payment provider is up to the developer and the use is fully specified by their implementation and documentation.

The goal of this arrangement is to encourage the fast evolution and benefits that are provided by open source software development, while providing proper economic feedback to the creators, e.g. through commercial clients. While some free and open source software (FOSS) proponents will argue that this arrangement is not open, software always exist as part of an economic process that needs to sustain itself (such as life itself). 

For example, an arrangement that requires for profit businesses to pay, while all other user groups can make use of the software in conventional open-source terms, can be considered superior to dual licensing in an open core model, because the business users are still free to benefit from the open source nature of the code, while the rest of the user groups can use the same features. In addition, the payment model is transparently documented in the code and can be executed automatically without additional bureaucratic overhead. Compared to SaaS models customers can rely on the availability and control over the functionality through the source code without counterparty risk, while the developer does not have setup a complicated silo to provide the functionality.

Mercurius currently supports a simple stripe based payment model. 

## Usage

Add mercurius to your dependencies:

~~~clojure
{:deps {mercurius/mercurius {:git/url "https://github.com/whilo/mercurius.git"
                             :git/sha "CURRENT_SHA"}}}
~~~

Set the following environment variables for your use case:

~~~bash
export PATH=/path/for/payment/database                 # by default ./mercurius
export USER_TAGS=private,non-profit                    # your user tags, by default "private"
export STRIPE_API_KEY=sk_test_Hrs6SAopgFPF0bZXSN3f6ELN # your STRIPE API KEY
~~~

The code invocations in the licensed code look like, here for a software that manages customers and has a monthly subscription:

~~~clojure
(require '[mercurius.core :as m])

;; immediate one time payment in the context of creating a customer, will charge every time when it is invoked
(m/pay "customer creation" ["for-profit"] 500 "usd" "stripe_connect_id_for_developer")

;; can be invoked anywhere, will only be charged once per month when called
(m/pay-monthly "saas monthly subscription" ["private"] 2500 "usd" "stripe_connect_id_for_developer")

;; get all payments from payment database as list of maps
(m/get-all-payments)
~~~

To demonstrate the idea we currently only provide Clojure bindings here, but mercurius is intended to be available for as many programming languages as possible.

## User tags

You need to follow this basic taxonomy to qualify your user type in the configuration. 

- "private": You are a private person and the code is not run in an institutional context.
- "non-profit": The code is run in a non-profit entity according to your local legislation.
- "for-profit": The code is run in a for profit entity according to your local legislation.

The taxonomy can be extended by users of the mercurius library to provide a more fine grained payment model. 

## Eclipse Public License with Transparent Payment Enforcement Additions

This is an augmented EPL 2.0 license requiring the licensee to comply with the payment functionality. In case where no payment functions are used it corresponds to vanilla EPL 2.0. It has not been tested in court yet and legal feedback is welcome. Please open an issue on this repository to get in contact. Other open source licenses could be augmented in a similar way, although copyleft licenses make more sense since they require application of the license also to derivative works.

~~~
Eclipse Public License - Version 2.0 with Transparent Payment Enforcement Additions

Copyright (c) <year> <copyright holders>

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0, which is available at
http://www.eclipse.org/legal/epl-2.0.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
Eclipse Public License for more details.

You should have received a copy of the Eclipse Public License along
with this program; if not, you can obtain a copy at eclipse.org.

Additional Terms for Retention and Transparent Enforcement of Paid Functions:

1. **Mandatory Retention of Paid Functions in the Source Code:** All payment function invocations within this software must remain intact and operational in the source code and any derivative works. These functions cannot be removed, circumvented, disabled, or otherwise rendered non-functional. Any attempt to alter the payment functionalities specifically to evade payment obligations is prohibited. This includes any modifications that would disable, bypass, or undermine the payment enforcement mechanisms. An exception are payment calls that are explicilty guarded by an expiration date that has come due. In this case the payment code can be removed or freely modified.

2. **Transparent Payment Mechanics:** The mechanisms for enforcing payment requirements for the Paid Functions are transparent and provided openly in the source code. All implementation details, including the payment amounts or plans, the specific purposes for each payment, and the user groups to which these payments apply, are described within the source code. Each Paid Function includes detailed in-code documentation that:
   - Specifies the payment amount or payment plan required.
   - Explains the purpose of the payment, linking it directly to the functionality it supports.
   - Identifies the user groups for whom the payment is applicable.

3. **Addition of New Paid Functions:** Developers are permitted to add new functionalities, including paid functions, to their derivative works. 

4. **Obligation to Maintain Payment Functionality during Execution:** Users of this software are required to maintain the functionality of the Paid Functions to the best of their abilities. Users must not intentionally disable, bypass, or otherwise interfere with the operation of the payment mechanisms. It is expected that users act in good faith to troubleshoot and rectify any issues that may prevent the proper functioning of the payment processes, provided that such troubleshooting is within their reasonable capability and does not involve modifications aimed at circumventing the intended payment obligations. If users encounter technical difficulties with the Paid Functions that are beyond their ability to resolve, they are required to report these issues to the maintainers of the software promptly, ensuring that efforts are made to restore functionality without undermining the payment mechanisms.

All other terms and conditions of the Eclipse Public License 2.0 apply to this software and its derivative works. Failure to comply with these additional terms constitutes a material breach of the license and may subject the violator to legal action.
~~~

## License

Copyright © 2024 Christian Weilbach

Distributed under the MIT license.
