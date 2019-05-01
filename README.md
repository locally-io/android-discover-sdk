# Locally DiscoverSDK

This document outlines the steps taken to integrate the DiscoverSDK to an Android application.

## Requirements

  - Android Studio
  - API 19: Android 4.4 (Kitkat) or above
  - Locally Keys
  
## Usage
### Initialize

Initialization is required in order to use the _DiscoverSDK_ features:

```Java
        class Activity : AppCompatActivity() {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_layout)
                DiscoverSDK.init(this)
                ...
            }
        }
```

### Login

    
After initializing, you may log in to the _**Locally platform**_ using your app keys.

```Java
        private fun performLogin(){
            DiscoverSDK.login("YOUR APP USERNAME", "YOUR APP PASSWORD") { response -> 
                when(response) {
                    true -> { /* login success */ }
                    else -> { /* login error */ }
                }
            }
        }
```

### Monitoring
   
DiscoverSDK provides a monitor and keep tracking the user device position.

```Java
        DiscoverSDK.monitor(object: DiscoverSDKDelegate {
                override var shouldSendRecords: Boolean = true

                override fun didUpdateRecords(records: JSONArray) {
                    println("Records updated: ${records.length()}")
                }

                override fun didReachThreshold() {
                    println("Pushing records")
                }
        })
```

for more info, please visit our [developer web site.](https://locally.io/developers/)

## Author
urosas@sahuarolabs.com

## License
DiscoverSDK is available under the MIT license. See the LICENSE file for more info.
