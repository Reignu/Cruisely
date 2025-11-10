--language_types start
--language_types 1
insert into language_types(id, name)
values (-1, 'EN');
--language_types end

--alter_types start
--alter_types 1
insert into alter_types(id, name)
values (-1, 'UPDATE');
--alter_types 2
insert into alter_types(id, name)
values (-2, 'INSERT');
--alter_types 3
insert into alter_types(id, name)
values (-3, 'DELETE');
--alter_types end

-- accounts start ||| password is abcABC123*
insert into accounts(id, first_name, second_name, login, email, password_hash, confirmed, active, dark_mode,
                     last_incorrect_authentication_date_time, last_incorrect_authentication_logical_address,
                     last_correct_authentication_date_time, last_correct_authentication_logical_address,
                     language_type_id,
                     creation_date_time, last_alter_date_time, alter_type_id, created_by_id, altered_by_id, version)
values (-1, 'Richard', 'Branson', 'rbranson', 'rbranson@gmail.com',
        '70417c6b38327eddd1f5ee5633d8b90ab06398ebb4461c8ebcdc5b22bf7a6578',
        true, true, false, null, null, null, null, -1, now(), now(), -2, -1, -1, 0);

insert into accounts(id, first_name, second_name, login, email, password_hash, confirmed, active, dark_mode,
                     last_incorrect_authentication_date_time, last_incorrect_authentication_logical_address,
                     last_correct_authentication_date_time, last_correct_authentication_logical_address,
                     language_type_id,
                     creation_date_time, last_alter_date_time, alter_type_id, created_by_id, altered_by_id, version)
values (-2, 'Elon', 'Musk', 'emusk', 'emusk@gmail.com',
        '70417c6b38327eddd1f5ee5633d8b90ab06398ebb4461c8ebcdc5b22bf7a6578',
        true, true, false, null, null, null, null, -1, now(), now(), -2, -2, -2, 0);

insert into accounts(id, first_name, second_name, login, email, password_hash, confirmed, active, dark_mode,
                     last_incorrect_authentication_date_time, last_incorrect_authentication_logical_address,
                     last_correct_authentication_date_time, last_correct_authentication_logical_address,
                     language_type_id,
                     creation_date_time, last_alter_date_time, alter_type_id, created_by_id, altered_by_id, version)
values (-3, 'Jeff', 'Bezos', 'jbezos', 'jbezos@gmail.com',
        '70417c6b38327eddd1f5ee5633d8b90ab06398ebb4461c8ebcdc5b22bf7a6578',
        true, true, false, null, null, null, null, -1, now(), now(), -2, -3, -3, 0);

insert into accounts(id, first_name, second_name, login, email, password_hash, confirmed, active, dark_mode,
                     last_incorrect_authentication_date_time, last_incorrect_authentication_logical_address,
                     last_correct_authentication_date_time, last_correct_authentication_logical_address,
                     language_type_id,
                     creation_date_time, last_alter_date_time, alter_type_id, created_by_id, altered_by_id, version)
values (-4, 'Mark', 'Zuckerberg', 'mzuckerberg', 'mzuckerberg@gmail.com',
        '70417c6b38327eddd1f5ee5633d8b90ab06398ebb4461c8ebcdc5b22bf7a6578',
        true, true, false, null, null, null, null, -1, now(), now(), -2, -4, -4, 0);
insert into accounts(id, first_name, second_name, login, email, password_hash, confirmed, active, dark_mode,
                     last_incorrect_authentication_date_time, last_incorrect_authentication_logical_address,
                     last_correct_authentication_date_time, last_correct_authentication_logical_address,
                     language_type_id,
                     creation_date_time, last_alter_date_time, alter_type_id, created_by_id, altered_by_id, version)
values (-5, 'John', 'Smith', 'jsmith', 'smith@gmail.com',
        '70417c6b38327eddd1f5ee5633d8b90ab06398ebb4461c8ebcdc5b22bf7a6578',
        true, true, false, null, null, null, null, -1, now(), now(), -2, -5, -5, 0);
insert into accounts(id, first_name, second_name, login, email, password_hash, confirmed, active, dark_mode,
                     last_incorrect_authentication_date_time, last_incorrect_authentication_logical_address,
                     last_correct_authentication_date_time, last_correct_authentication_logical_address,
                     language_type_id,
                     creation_date_time, last_alter_date_time, alter_type_id, created_by_id, altered_by_id, version)
values (-6, 'Adam', 'Ungier', 'aungier', 'ungier@gmail.com',
        '70417c6b38327eddd1f5ee5633d8b90ab06398ebb4461c8ebcdc5b22bf7a6578',
        true, false, false, null, null, null, null, -1, now(), now(), -2, -6, -6, 0);

insert into accounts(id, first_name, second_name, login, email, password_hash, confirmed, active, dark_mode,
                     last_incorrect_authentication_date_time, last_incorrect_authentication_logical_address,
                     last_correct_authentication_date_time, last_correct_authentication_logical_address,
                     language_type_id,
                     creation_date_time, last_alter_date_time, alter_type_id, created_by_id, altered_by_id, version)
values (-7, 'Bob', ' Dylan', 'bdylan', 'dylan@gmail.com',
        '70417c6b38327eddd1f5ee5633d8b90ab06398ebb4461c8ebcdc5b22bf7a6578',
        true, true, false, null, null, null, null, -1, now(), now(), -2, -7, -7, 0);

-- accounts end
--
--
--
-- access_levels start
insert into access_levels (id, access_level, account_id, enabled, creation_date_time, last_alter_date_time,
                           created_by_id, altered_by_id, alter_type_id, version)
values (-1, 'Administrator', -1, true, now(), now(), -1, -1, -2, 0);

insert into access_levels (id, access_level, account_id, enabled, creation_date_time, last_alter_date_time,
                           created_by_id, altered_by_id, alter_type_id, version)
values (-2, 'Client', -1, true, now(), now(), -2, -2, -2, 0);

insert into access_levels (id, access_level, account_id, enabled, creation_date_time, last_alter_date_time,
                           created_by_id, altered_by_id, alter_type_id, version)
values (-3, 'Client', -2, true, now(), now(), -3, -3, -2, 0);

insert into access_levels (id, access_level, account_id, enabled, creation_date_time, last_alter_date_time,
                           created_by_id, altered_by_id, alter_type_id, version)
values (-4, 'BusinessWorker', -3, true, now(), now(), -3, -3, -2, 0);

insert into access_levels (id, access_level, account_id, enabled, creation_date_time, last_alter_date_time,
                           created_by_id, altered_by_id, alter_type_id, version)
values (-5, 'Moderator', -4, true, now(), now(), -4, -4, -2, 0);

insert into access_levels (id, access_level, account_id, enabled, creation_date_time, last_alter_date_time,
                           created_by_id, altered_by_id, alter_type_id, version)
values (-6, 'BusinessWorker', -5, true, now(), now(), -5, -5, -2, 0);

insert into access_levels (id, access_level, account_id, enabled, creation_date_time, last_alter_date_time,
                           created_by_id, altered_by_id, alter_type_id, version)
values (-7, 'Administrator', -6, false, now(), now(), -6, -6, -2, 0);

insert into access_levels (id, access_level, account_id, enabled, creation_date_time, last_alter_date_time,
                           created_by_id, altered_by_id, alter_type_id, version)
values (-8, 'Moderator', -6, true, now(), now(), -6, -6, -2, 0);


insert into access_levels (id, access_level, account_id, enabled, creation_date_time, last_alter_date_time,
                           created_by_id, altered_by_id, alter_type_id, version)
values (-9, 'BusinessWorker', -7, true, now(), now(), -7, -7, -2, 0);
-- access_levels end
--
--
--
-- administrators start
insert into administrators (id)
values (-1);
insert into administrators (id)
values (-7);
-- administrators end
--
--
--
-- addresses start
insert into addresses(id, house_number, street, postal_code, city, country,
                      creation_date_time, last_alter_date_time, alter_type_id, created_by_id, altered_by_id, version)
values (-1, '1', 'street', '30-300', 'London', 'United Kingdom', now(), now(), -2, -2, -2, 0);

insert into addresses(id, house_number, street, postal_code, city, country,
                      creation_date_time, last_alter_date_time, alter_type_id, created_by_id, altered_by_id, version)
values (-2, '1', 'street', '30-300', 'Manchester', 'United Kingdom', now(), now(), -2, -3, -3, 0);

insert into addresses(id, house_number, street, postal_code, city, country,
                      creation_date_time, last_alter_date_time, alter_type_id, created_by_id, altered_by_id, version)
values (-3, '45', 'street', '92-300', 'Glasgow', 'United Kingdom', now(), now(), -2, -5, -5, 0);
-- addresses end
--
--
--
--companies start
insert into companies(id, name, address_id, phone_number, nip, creation_date_time, last_alter_date_time, alter_type_id,
                      created_by_id,
                      altered_by_id, version)
values (-1, 'Company', -1, '604203145', 1265485965, now(), now(), -2, -1, -1, 0);
insert into companies(id, name, address_id, phone_number, nip, creation_date_time, last_alter_date_time, alter_type_id,
                      created_by_id,
                      altered_by_id, version)
values (-2, 'GroveStFamily', -2, '787123100', 2354685748, now(), now(), -2, -1, -1, 0);
insert into companies(id, name, address_id, phone_number, nip, creation_date_time, last_alter_date_time, alter_type_id,
                      created_by_id,
                      altered_by_id, version)
values (-3, 'BeautifulCompany', -2, '585230492', 9568545875, now(), now(), -2, -1, -1, 0);
--companies end
--
--
--
-- business_workers start
insert into business_workers (id, phone_number, company_id, confirmed)
values (-4, '887552642', -2, true);

insert into business_workers (id, phone_number, company_id, confirmed)
values (-6, '690066055', -3, false);
insert into business_workers (id, phone_number, company_id, confirmed)
values (-9, '690066055', -1, true);
-- business_workers end
--
--
--
-- clients start
insert into clients(id, phone_number, home_address_id)
values (-2, '123456789', -1);

insert into clients(id, phone_number, home_address_id)
values (-3, '123456789', -2);

-- clients end
--
--
--
-- moderators start
insert into moderators(id)
values (-5);

insert into moderators(id)
values (-8);
-- moderators end
--
--
------------Insert Into MOW---------------
--
--
--cruise_addresses start
insert into cruise_addresses(id, street, street_number, harbor_name, city_name, country_name,
                             creation_date_time, last_alter_date_time, alter_type_id, created_by_id, altered_by_id,
                             version)
values (-1, 'street Company', '321', 'ManchesterHabor', 'Manchester', 'United Kingdom', now(), now(), -2, -3, -3, 0);
insert into cruise_addresses(id, street, street_number, harbor_name, city_name, country_name,
                             creation_date_time, last_alter_date_time, alter_type_id, created_by_id, altered_by_id,
                             version)
--
--
--
--cruise_pictures start
--//TO DO - add images
insert into cruise_pictures(id, img_name, img, creation_date_time, last_alter_date_time, alter_type_id, created_by_id,
                            altered_by_id, version)
values (-1, 'LowRidder.jpg', '#', now(), now(), -2, -1, -1, 0);
insert into cruise_pictures(id, img_name, img, creation_date_time, last_alter_date_time, alter_type_id, created_by_id,
                            altered_by_id, version)
values (-2, 'kitty.jpg', '#', now(), now(), -2, -1, -1, 0);
insert into cruise_pictures(id, img_name, img, creation_date_time, last_alter_date_time, alter_type_id, created_by_id,
                            altered_by_id, version)
values (-3, 'PearlHarbor.jpg', '#', now(), now(), -2, -1, -1, 0);

--cruise_pictures end
--
--
--
--cruises_groups start
insert into cruises_groups(id, company_id, name, number_of_seats, price, start_address_id, average_rating, active,
                           creation_date_time, last_alter_date_time, alter_type_id, created_by_id, altered_by_id,
                           version, uuid, description)
values (-1, -1, 'Adventure', 24, 52, -1, 2.5, true, now(), now(), -2, -1, -1, 0, 'dd0ea0ca-bbac-11eb-8529-0242ac130003',
        'The most Beautiful tour');
insert into cruises_groups(id, company_id, name, number_of_seats, price, start_address_id, average_rating, active,
                           creation_date_time, last_alter_date_time, alter_type_id, created_by_id, altered_by_id,
                           version, uuid, description)
values (-2, -3, 'Santa Maria Beach', 42, 152, -2, 4.5, true, now(), now(), -2, -1, -1, 0,
        'dd1ea0ca-bbac-11eb-8529-0242ac130003', 'Beautiful tour');
insert into cruises_groups(id, company_id, name, number_of_seats, price, start_address_id, average_rating, active,
                           creation_date_time, last_alter_date_time, alter_type_id, created_by_id, altered_by_id,
                           version, uuid, description)
values (-3, -2, 'Beautiful Sandy Shores', 31, 502, -4, 5, true, now(), now(), -2, -1, -1, 0,
        'dd2ea0ca-bbac-11eb-8529-0242ac130003', 'Beautiful Beautiful tour');
--cruises_groups end
--
--
--
--cruises start
insert into cruises(id, uuid, start_date, end_date, active, cruises_group_id,
                    creation_date_time,
                    last_alter_date_time, alter_type_id, created_by_id,
                    altered_by_id, version, published)
values (-1, '581d626f-d421-47dd-89ef-b41bc30aa36c', '2021-06-30 13:35:22.650752', '2021-07-05 19:00:00.650752', true, -1, now(), now(), -2,
       -1, -1, 0, true);
insert into cruises(id, uuid, start_date, end_date, active, cruises_group_id,
                    creation_date_time,
                    last_alter_date_time, alter_type_id, created_by_id,
                    altered_by_id, version, published)
values (-2, '181d626f-d421-47dd-89ef-b41bc30aa36c', '2021-06-29 08:00:00.650752', '2021-07-15 20:00:00.650752', false, -2, now(),
        now(), -2, -1, -1, 0, true);
insert into cruises(id, uuid, start_date, end_date, active, cruises_group_id,
                    creation_date_time,
                    last_alter_date_time, alter_type_id, created_by_id,
                    altered_by_id, version, published)
values (-3, '481d626f-d421-47dd-89ef-b41bc30aa36c','2021-07-01 12:30:00.650752', '2021-07-25 12:00:00.650752', true, -3, now(),
        now(), -2, -1, -1, 0, true);

insert into cruises(id, uuid, start_date, end_date, active, cruises_group_id,
                    creation_date_time,
                    last_alter_date_time, alter_type_id, created_by_id,
                    altered_by_id, version, published)
values (-4, '72956ba6-d451-11eb-b8bc-0242ac130003','2021-07-01 12:30:00.650752', '2021-07-25 12:00:00.650752', true, -1, now(),
        now(), -2, -1, -1, 0, true);
insert into cruises(id, uuid, start_date, end_date, active, cruises_group_id,
                    creation_date_time,
                    last_alter_date_time, alter_type_id, created_by_id,
                    altered_by_id, version, published)
values (-5, '72956dfe-d451-11eb-b8bc-0242ac130003','2021-06-30 12:30:00.650752', '2021-07-18 12:00:00.650752', true, -1, now(),
        now(), -2, -1, -1, 0, true);

insert into cruises(id, uuid, start_date, end_date, active, cruises_group_id,
                    creation_date_time,
                    last_alter_date_time, alter_type_id, created_by_id,
                    altered_by_id, version, published)
values (-6, '1fce60ae-d455-11eb-b8bc-0242ac130003','2021-06-30 12:30:00.650752', '2021-07-18 12:00:00.650752', true, -1, now(),
        now(), -2, -1, -1, 0, true);

insert into cruises(id, uuid, start_date, end_date, active, cruises_group_id,
                    creation_date_time,
                    last_alter_date_time, alter_type_id, created_by_id,
                    altered_by_id, version, published)
values (-7, 'a454c84e-d451-11eb-b8bc-0242ac130003','2021-06-30 12:30:00.650752', '2021-07-18 12:00:00.650752', true, -2, now(),
        now(), -2, -1, -1, 0, true);

insert into cruises(id, uuid, start_date, end_date, active, cruises_group_id,
                    creation_date_time,
                    last_alter_date_time, alter_type_id, created_by_id,
                    altered_by_id, version, published)
values (-8, 'a454c786-d451-11eb-b8bc-0242ac130003','2021-07-04 12:30:00.650752', '2021-07-18 12:00:00.650752', true, -2, now(),
        now(), -2, -1, -1, 0, true);

insert into cruises(id, uuid, start_date, end_date, active, cruises_group_id,
                    creation_date_time,
                    last_alter_date_time, alter_type_id, created_by_id,
                    altered_by_id, version, published)
values (-9, '1804318c-d45a-11eb-b8bc-0242ac130003','2021-07-05 12:30:00.650752', '2021-07-19 12:00:00.650752', true, -2, now(),
        now(), -2, -1, -1, 0, true);
insert into cruises(id, uuid, start_date, end_date, active, cruises_group_id,
                    creation_date_time,
                    last_alter_date_time, alter_type_id, created_by_id,
                    altered_by_id, version, published)
values (-17, '98c3c4f4-d45a-11eb-b8bc-0242ac130003','2021-07-09 12:30:00.650752', '2021-07-19 12:00:00.650752', true, -3, now(),
        now(), -2, -1, -1, 0, true);
--cruises end
--
--
--
--attractions start
insert into attractions(id, name, description, price, number_of_seats, has_free_spots, cruise_id, creation_date_time,
                        last_alter_date_time, alter_type_id, created_by_id,
                        altered_by_id, version, uuid)
values (-1, 'Bungee2', 'Bungee jump', 23, 20, true, -1, now(), now(), -2, -1, -1, 0,
        'e42ea0ca-bbac-11eb-8529-0242ac130003');
insert into attractions(id, name, description, price, number_of_seats, has_free_spots, cruise_id, creation_date_time,
                        last_alter_date_time, alter_type_id, created_by_id,
                        altered_by_id, version, uuid)
values (-2, 'Scuba Diving2', 'Scuba diving in the sea', 223, 20, true, -2, now(), now(), -2, -1, -1, 0,
        'e42e9fd0-bbac-11eb-8529-0242ac130003');
insert into attractions(id, name, description, price, number_of_seats, has_free_spots, cruise_id, creation_date_time,
                        last_alter_date_time, alter_type_id, created_by_id,
                        altered_by_id, version, uuid)
values (-3, 'Romantic dinner2', 'Romantic dinner on the most beautiful Sandy Shores', 1203, 20, true, -3, now(), now(),
       -2, -1, -1, 0, 'a454c6b4-d451-11eb-b8bc-0242ac130003');
insert into attractions(id, name, description, price, number_of_seats, has_free_spots, cruise_id, creation_date_time,
                        last_alter_date_time, alter_type_id, created_by_id,
                        altered_by_id, version, uuid)
values (-4, 'Susnset at the wild beach', 'Unforgetable experience', 222, 100, true, -3, now(), now(),
        -2, -1, -1, 0, 'b4a61c10-d452-11eb-b8bc-0242ac130003');

insert into attractions(id, name, description, price, number_of_seats, has_free_spots, cruise_id, creation_date_time,
                        last_alter_date_time, alter_type_id, created_by_id,
                        altered_by_id, version, uuid)
values (-5, 'Karaoke night', 'Spent a wild night dancing and singing to the greatest hits', 10, 20, true, -1, now(), now(),
        -2, -1, -1, 0, 'b4a61e40-d452-11eb-b8bc-0242ac130003');

insert into attractions(id, name, description, price, number_of_seats, has_free_spots, cruise_id, creation_date_time,
                        last_alter_date_time, alter_type_id, created_by_id,
                        altered_by_id, version, uuid)
values (-6, 'Stand up with the stars', 'Comedian show for all of your family', 55, 60, true, -5, now(), now(),
       -2, -1, -1, 0, '98c3c5da-d45a-11eb-b8bc-0242ac130003');
insert into attractions(id, name, description, price, number_of_seats, has_free_spots, cruise_id, creation_date_time,
                        last_alter_date_time, alter_type_id, created_by_id,
                        altered_by_id, version, uuid)
values (-7, 'Golf lessons', 'Golf lessons on the ship ', 150, 20, true, -17, now(), now(),
        -2, -1, -1, 0, '98c3c6ac-d45a-11eb-b8bc-0242ac130003');
insert into attractions(id, name, description, price, number_of_seats, has_free_spots, cruise_id, creation_date_time,
                        last_alter_date_time, alter_type_id, created_by_id,
                        altered_by_id, version, uuid)
values (-8, 'Visiting a lonely island', 'Visit a mysterious lonely island', 100, 10, true, -2, now(), now(),
        -2, -1, -1, 0, '98c3c828-d45a-11eb-b8bc-0242ac130003');
--attractions end
--
--
--
--reservation start
insert into reservations(id, uuid, client_id, number_of_seats, cruise_id, price, creation_date_time,
                         last_alter_date_time,
                         alter_type_id, created_by_id,
                         altered_by_id, version)
values (-1, '6177391a-d453-11eb-b8bc-0242ac130003', -2, 2, -1, 50, now(), now(), -2, -1, -1, 0);
insert into reservations(id, uuid, client_id, number_of_seats, cruise_id, price, creation_date_time,
                         last_alter_date_time,
                         alter_type_id, created_by_id,
                         altered_by_id, version)
values (-2, '61773d98-d453-11eb-b8bc-0242ac130003', -2, 5, -2, 20, now(), now(), -2, -1, -1, 0);
insert into reservations(id, uuid, client_id, number_of_seats, cruise_id, price, creation_date_time,
                         last_alter_date_time,
                         alter_type_id, created_by_id,
                         altered_by_id, version)
values (-3, '61773eba-d453-11eb-b8bc-0242ac130003',-2, 1, -3, 30, now(), now(), -2, -1, -1, 0);

insert into reservations(id, uuid, client_id, number_of_seats, cruise_id, price, creation_date_time,
                         last_alter_date_time,
                         alter_type_id, created_by_id,
                         altered_by_id, version)
values (-4, '9456de6c-d453-11eb-b8bc-0242ac130003',-3, 5, -1, 30, now(), now(), -2, -1, -1, 0);
--reservation end
--
--
--
--ratings start
insert into ratings(id, account_id, cruise_group_id, rating, creation_date_time, last_alter_date_time, alter_type_id,
                    created_by_id,
                    altered_by_id, version, uuid)
values (-1, -2, -1, 3, now(), now(), -2, -1, -1, 0, '9456d84a-d453-11eb-b8bc-0242ac130003');
insert into ratings(id, account_id, cruise_group_id, rating, creation_date_time, last_alter_date_time, alter_type_id,
                    created_by_id,
                    altered_by_id, version, uuid)
values (-2, -1, -2, 4.5, now(), now(), -2, -1, -1, 0, '9456da84-d453-11eb-b8bc-0242ac130003');
insert into ratings(id, account_id, cruise_group_id, rating, creation_date_time, last_alter_date_time, alter_type_id,
                    created_by_id,
                    altered_by_id, version, uuid)
values (-3, -1, -3, 5, now(), now(), -2, -1, -1, 0, '9456db74-d453-11eb-b8bc-0242ac130003');
--ratings end
--
--
--
--
--cruises_group_pictures start
 insert into cruises_group_pictures (cruises_group_id, cruise_picture_id)
 values (-3,-1);
insert into cruises_group_pictures (cruises_group_id, cruise_picture_id)
 values (-2,-2);
insert into cruises_group_pictures (cruises_group_id, cruise_picture_id)
values (-1,-3);

--cruises_group_pictures end
--
--
--
--reservation_attractions start
insert into reservation_attractions (reservation_id, attraction_id)
values (-1, -1);
insert into reservation_attractions (reservation_id, attraction_id)
values (-2, -2);
insert into reservation_attractions (reservation_id, attraction_id)
values (-3, -3);
--reservation_attractions end
