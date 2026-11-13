package ca.mcgill.ecse321.gameorganizer.repositories;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ca.mcgill.ecse321.gameorganizer.models.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    Optional<Event> findEventById(UUID id);

    List<Event> findEventByTitle(String title);

    List<Event> findEventByTitleContaining(String title);

    List<Event> findEventByDateTime(Date dateTime);

    List<Event> findEventByLocation(String location);

    List<Event> findEventByLocationContaining(String location);

    List<Event> findEventByDescription(String description);

    List<Event> findEventByMaxParticipants(int maxParticipants);

    List<Event> findEventByFeaturedSetId(int featuredSetId);

    List<Event> findEventByFeaturedSetName(String featuredSetName);

    List<Event> findEventByHostId(int hostId);

    List<Event> findEventByHostName(String hostUsername);

    List<Event> findEventByHostEmail(String hostEmail);

    void deleteAllByFeaturedSetId(int setId);
}
