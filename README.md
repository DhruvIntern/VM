Scalable Consistent Synthetic Data Generator
============================================

This project provides a scalable, consistent synthetic data generator based on regex patterns with a novel GraphQL-like representation for the data model. The generator is designed to produce high-quality synthetic data for software product development and testing, ensuring consistency across entities and relationships within a data model.

Features
--------

*   Consistent data generation across entities and relationships
*   Support for custom data types with regex patterns
*   Enum support for generating data from a predefined set of values
*   Highly expressive and nearly human-readable data model representation
*   Efficient performance, generating large datasets in a short amount of time

Getting Started
---------------

### Prerequisites

*   Java 8 or later
*   Maven 3.6.3 or later

### Installation

1.  Clone this repository:
    
    bashCopy code
    
    `git clone https://github.com/vaidhyamegha/data-generator.git` 
    
2.  Navigate to the project directory:
    
    bashCopy code
    
    `cd data-generator` 
    
3.  Build the project using Maven:
    
    Copy code
    
    `mvn clean install` 
    

### Usage

1.  Create a GraphQL-like specification file for your data model (e.g., `datamodel.graphql`).
    
2.  Run the data generator with the following command:
    
    cssCopy code
    
    `java -jar target/DataGenerator-1.0-SNAPSHOT.jar --spec datamodel.graphql` 
    
    This command will generate synthetic data based on the provided data model and write the results to CSV files.
    

Documentation
-------------

Refer to the [paper](PAPER_LINK) for detailed information about the design, implementation, and evaluation of the scalable consistent synthetic data generator.

Contributing
------------

We welcome contributions to improve and expand the capabilities of the synthetic data generator. If you have any ideas or suggestions, please feel free to open an issue or submit a pull request.

License
-------

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.