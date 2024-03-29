//
//  Network.swift
//  CarelineSwifty
//
//  Created by formando on 28/11/2023.
//

import Combine
import Foundation

class APIToken: ObservableObject {
    var bearerToken: String = ""
    var userName: String = ""
    var userEmail: String = ""
    var userNus: String = ""
    var userId: String = ""
    
    func signInPatient(nus: String, password: String, completion: @escaping (Error?) -> ()) {
        guard let url = URL(string: "http://10.20.229.55/api/signin/patient") else {
            print("Invalid URL")
            return
        }

        let requestData: [String: Any] = [
            "nus": nus,
            "password": password
        ]

        do {
            // Convert the requestData to JSON data
            let jsonData = try JSONSerialization.data(withJSONObject: requestData)

            // Create the URLRequest with the specified URL and method
            var request = URLRequest(url: url)
            request.httpMethod = "POST"

            // Set the request body with the JSON data
            request.httpBody = jsonData

            // Set the content type to JSON
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
                         

            // Create a URLSession task for the request
                URLSession(configuration: NetworkConfigurator.getSessionConfiguration()).dataTask(with: request) { [weak self] data, response, error in
                if let error = error {
                    print("Error: \(error)")
                    completion(error)
                    return
                }

                guard let httpResponse = response as? HTTPURLResponse else {
                    completion(nil)
                    return
                }

                if httpResponse.statusCode == 200, let data = data {
                    do {
                        if let json = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any],
                           let token = json["token"] as? String {
                            self?.bearerToken = token
                            self?.userNus = nus
                            completion(nil)
                            return
                        }
                    } catch {
                        print("Error parsing JSON: \(error)")
                        completion(error)
                        return
                    }
                }
                    
                completion(NSError(domain:"", code: httpResponse.statusCode, userInfo:nil))
            }.resume()
        } catch {
            print("Error converting requestData to JSON data: \(error)")
            completion(error)
        }
    }
    
    func getPatient(completion: @escaping (Error?) -> ()) {
        guard let url = URL(string: "http://10.20.229.55/api/patients/nus/\(self.userNus)") else {
            print("Invalid URL")
            return
        }

        do {
            // Create the URLRequest with the specified URL and method
            var request = URLRequest(url: url)
            request.httpMethod = "GET"

            // Set the content type to JSON
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            request.addValue("Bearer \(bearerToken)", forHTTPHeaderField: "Authorization")
                         

            // Create a URLSession task for the request
            URLSession(configuration: NetworkConfigurator.getSessionConfiguration()).dataTask(with: request) { [weak self] data, response, error in
                if let error = error {
                    print("Error: \(error)")
                    completion(error)
                    return
                }

                guard let httpResponse = response as? HTTPURLResponse else {
                    completion(nil)
                    return
                }

                if httpResponse.statusCode == 200, let data = data {
                    do {
                       let json = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any]
                        
                        print(json)
                        
                        if let id = json?["id"] as? Int {
                            print("Id: \(id)")
                            self?.userId = "\(id)"
                        } else {
                            print("Error: Unable to extract id from the JSON.")
                        }
                        
                        if let name = json?["name"] as? String {
                            print("Name: \(name)")
                            self?.userName = name
                        } else {
                            print("Error: Unable to extract name from the JSON.")
                        }
                        
                        if let email = json?["email"] as? String {
                            print("Email: \(email)")
                            self?.userEmail = email
                        } else {
                            print("Error: Unable to extract email from the JSON.")
                        }
                        
                        
                        
                    } catch {
                        print("Error parsing JSON: \(error)")
                        completion(error)
                        return
                    }
                }
                    
                completion(NSError(domain:"", code: httpResponse.statusCode, userInfo:nil))
            }.resume()
        } catch {
            print("Error converting requestData to JSON data: \(error)")
            completion(error)
        }
    }
}
    
    
    
class APITemperatureGET: ObservableObject {
    var bearerToken: String = ""
    
    func getTemperature(completion: @escaping (Double) -> ()) {
        guard let url = URL(string: "http://10.20.229.180/temperature") else {fatalError("Incorrect URL")}
        
        let urlRequest = URLRequest(url: url)
        
        let dataTask = URLSession(configuration: NetworkConfigurator.getSessionConfiguration()).dataTask(with: urlRequest) { (data, response, error) in
            if let error = error {
                print("Request error.", error)
                return
            }
            
            guard let response = response as? HTTPURLResponse else {return}
            
            if response.statusCode == 200 {
                guard let value = data else {return }
                
                DispatchQueue.main.async {
                    do {
                        let json = try JSONSerialization.jsonObject(with: value, options: []) as? [String: Any]
                        
                        if let temperatureValue = json?["value"] as? Double {
                            print("Temperature: \(temperatureValue)")
                            completion(temperatureValue)
                        } else {
                            print("Error: Unable to extract temperature value from the JSON.")
                        }
                    } catch {
                        print("Error \(error)")
                    }
                }
            }
        }
        
        dataTask.resume()
    }
}

class APITemperaturePOST: ObservableObject {
    var bearerToken: String = ""
    var userId: String = ""
    
    func makeTemperaturePostRequest(temperature: Double, completion: @escaping (Error?) -> ()){
        guard let url = URL(string: "http://10.20.229.55/api/patients/\(userId)/temperatures") else {
            print("Invalid URL")
            return
        }
        
        let formattedTemperature = String(format: "%.2f", temperature)
        let requestData: [String: Any] = [
            "temperature": formattedTemperature
        ]
        
        do {
            // Convert the requestData to JSON data
            let jsonData = try JSONSerialization.data(withJSONObject: requestData)

            // Create the URLRequest with the specified URL and method
            var request = URLRequest(url: url)
            request.httpMethod = "POST"

            // Set the request body with the JSON data
            request.httpBody = jsonData

            // Set the content type to JSON
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            
            // Set the authorization header with the bearer token
            request.addValue("Bearer \(bearerToken)", forHTTPHeaderField: "Authorization")
            
            print("RequestData: \(requestData)")
            print("request: \(request)")
            
            // Create a URLSession task for the request
            URLSession(configuration: NetworkConfigurator.getSessionConfiguration()).dataTask(with: request) { data, response, error in
                if let error = error {
                    print("Error: \(error)")
                } else if let response = response as? HTTPURLResponse {
                    if(response.statusCode == 201) {
                        print("POST request successful")
                    } else {
                        print("POST request failed with status code: \(response.statusCode)")
                    }
                }
            }.resume()
        } catch {
            print("Error converting requestData to JSON data: \(error)")
        }
        
    }
}

class APIHeartbeatPOST: ObservableObject {
    var bearerToken: String = ""
    var userId: String = ""
    
    func makeHeartRatePostRequest(heartbeat: Int32, completion: @escaping (Error?) -> Void) {
        
        guard let url = URL(string: "http://10.20.229.55/api/patients/\(userId)/heartbeats") else {
            print("Invalid URL")
            return
        }
        
        let requestData: [String: Any] = [
            "heartbeat": String(heartbeat)
        ]
        
        do {
            // Convert the requestData to JSON data
            let jsonData = try JSONSerialization.data(withJSONObject: requestData)

            // Create the URLRequest with the specified URL and method
            var request = URLRequest(url: url)
            request.httpMethod = "POST"

            // Set the request body with the JSON data
            request.httpBody = jsonData

            // Set the content type to JSON
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            
            // Set the authorization header with the bearer token
            request.addValue("Bearer \(bearerToken)", forHTTPHeaderField: "Authorization")
            
            
            URLSession(configuration: NetworkConfigurator.getSessionConfiguration()).dataTask(with: request) { data, response, error in
                if let error = error {
                    print("Error: \(error)")
                } else if let response = response as? HTTPURLResponse {
                    if(response.statusCode == 201) {
                        print("POST request successful")
                        
                    } else {
                        print("POST request failed with status code: \(response.statusCode)")
                    }
                }
            }.resume()
        } catch {
            print("Error converting requestData to JSON data: \(error)")
        }
        
    }
}

struct HeartbeatContent: Codable {
    let content: [HeartbeatItem]
    let pageable: Pageable
    let last: Bool
    let totalElements: Int
    let totalPages: Int
    let size: Int
    let number: Int
    let sort: Sort
    let first: Bool
    let numberOfElements: Int
    let empty: Bool
}

struct HeartbeatItem: Codable {
    let severity: String
    let heartbeat: Int
    let createdAt: String
    
    enum CodingKeys: String, CodingKey {
        case severity
        case heartbeat
        case createdAt = "created_at"
    }
}

struct TemperatureContent: Codable {
    let content: [TemperatureItem]
    let pageable: Pageable
    let last: Bool
    let totalElements: Int
    let totalPages: Int
    let size: Int
    let number: Int
    let sort: Sort
    let first: Bool
    let numberOfElements: Int
    let empty: Bool
}


struct TemperatureItem: Codable {
    let severity: String
    let temperature: Double
    let createdAt: String
    
    enum CodingKeys: String, CodingKey {
        case severity
        case temperature
        case createdAt = "created_at"
    }
}

struct Pageable: Codable {
    let pageNumber: Int
    let pageSize: Int
    let sort: Sort
    let offset: Int
    let paged: Bool
    let unpaged: Bool
}

struct Sort: Codable {
    let empty: Bool
    let sorted: Bool
    let unsorted: Bool
}

class APIHeartbeatGET: ObservableObject {
    var bearerToken: String = ""
    var userId: String = ""
    
    @Published var latestHeartbeatSeverity: String?
    @Published var latestHeartbeatValue: Int?
    @Published var latestHeartbeatCreatedAt: String?
    
    func makeHeartbeatGetRequest(completion: @escaping (Result<Void, Error>) -> ()) {
        print("URLLLLLLLLLL http://10.20.229.55/api/patients/\(userId)/heartbeats/latest?size=1")
        
        guard let url = URL(string: "http://10.20.229.55/api/patients/\(userId)/heartbeats/latest?size=1") else {
            print("Invalid URL")
            return
        }

        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        
        // Set the authorization header with the bearer token
        request.addValue("Bearer \(bearerToken)", forHTTPHeaderField: "Authorization")

        let task = URLSession(configuration: NetworkConfigurator.getSessionConfiguration()).dataTask(with: request) { data, response, error in
            if let error = error {
                completion(.failure(error))
                return
            }

            guard let httpResponse = response as? HTTPURLResponse,
                  (200...299).contains(httpResponse.statusCode) else {
                let statusCode = (response as? HTTPURLResponse)?.statusCode ?? -1
                completion(.failure(NSError(domain: "HTTP Error", code: statusCode, userInfo: nil)))
                return
            }

            do {
                if let data = data {
                    let decoder = JSONDecoder()
                    let heartbeatContent = try decoder.decode(HeartbeatContent.self, from: data)
                    if let firstHeartbeat = heartbeatContent.content.first {
                        self.latestHeartbeatSeverity = firstHeartbeat.severity
                        self.latestHeartbeatValue = firstHeartbeat.heartbeat
                        self.latestHeartbeatCreatedAt = firstHeartbeat.createdAt
                        completion(.success(()))
                        print("Latest heartbeat Severity: \(self.latestHeartbeatSeverity)")
                        print("Latest heartbeat Value: \(self.latestHeartbeatValue)")
                        print("Latest heartbeat Created At: \(self.latestHeartbeatCreatedAt)")
                    }
                }
            } catch {
                completion(.failure(error))
            }
        }
        
        task.resume()
    }
}

class APITemperatureGETCareline: ObservableObject {
    var bearerToken: String = ""
    var userId: String = ""
    var latestTemperatureSeverity: String?
    var latestTemperatureValue: Double?
    var latestTemperatureCreatedAt: String?
    
    func makeTemperatureGetRequest(completion: @escaping (Result<Void, Error>) -> ()) {
        guard let url = URL(string: "http://10.20.229.55/api/patients/\(userId)/temperatures/latest?size=1") else {
            print("Invalid URL")
            return
        }

        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        
        // Set the authorization header with the bearer token
        request.addValue("Bearer \(bearerToken)", forHTTPHeaderField: "Authorization")

        let task = URLSession(configuration: NetworkConfigurator.getSessionConfiguration()).dataTask(with: request) { data, response, error in
            if let error = error {
                completion(.failure(error))
                return
            }

            guard let httpResponse = response as? HTTPURLResponse,
                  (200...299).contains(httpResponse.statusCode) else {
                let statusCode = (response as? HTTPURLResponse)?.statusCode ?? -1
                completion(.failure(NSError(domain: "HTTP Error", code: statusCode, userInfo: nil)))
                return
            }

            do {
                if let data = data {
                    let decoder = JSONDecoder()
                    let temperatureContent = try decoder.decode(TemperatureContent.self, from: data)
                    if let firstTemperature = temperatureContent.content.first {
                        self.latestTemperatureSeverity = firstTemperature.severity
                        self.latestTemperatureValue = firstTemperature.temperature
                        self.latestTemperatureCreatedAt = firstTemperature.createdAt
                        completion(.success(()))
                        print("Latest temperature Severity: \(self.latestTemperatureSeverity)")
                        print("Latest temperature Value: \(self.latestTemperatureValue)")
                        print("Latest temperature Created At: \(self.latestTemperatureCreatedAt)")
                    }
                }
            } catch {
                completion(.failure(error))
            }
        }
        
        task.resume()
    }
}

class APITriagePOST: ObservableObject {
    var bearerToken: String = ""
    var userId: String = ""
    
    func makeTriagePostRequest(symptoms: String,temperature: Double,heartbeat: Int, completion: @escaping (Error?) -> Void) {
        guard let url = URL(string: "http://10.20.229.55/api/patients/\(userId)/triages") else {
            print("Invalid URL")
            return
        }
        
        let requestData: [String: Any] = [
            "heartbeat": String(heartbeat),
            "temperature": String(temperature),
            "symptoms": symptoms
        ]
        
        do {
            // Convert the requestData to JSON data
            let jsonData = try JSONSerialization.data(withJSONObject: requestData)

            // Create the URLRequest with the specified URL and method
            var request = URLRequest(url: url)
            request.httpMethod = "POST"

            // Set the request body with the JSON data
            request.httpBody = jsonData

            // Set the content type to JSON
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            
            // Set the authorization header with the bearer token
            request.addValue("Bearer \(bearerToken)", forHTTPHeaderField: "Authorization")

            // Create a URLSession task for the request
            URLSession(configuration: NetworkConfigurator.getSessionConfiguration()).dataTask(with: request) { data, response, error in
                if let error = error {
                    print("Error: \(error)")
                } else if let response = response as? HTTPURLResponse {
                    if(response.statusCode == 201) {
                        print("POST request successful")
                    } else {
                        print("POST request failed with status code: \(response.statusCode)")
                    }
                }
            }.resume()
        } catch {
            print("Error converting requestData to JSON data: \(error)")
        }
        
    }
}
